package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateVariantRequest;
import com.ptit.clone.dtos.request.UpdateVariantRequest;
import com.ptit.clone.dtos.response.ProductVariantResponse;
import com.ptit.clone.dtos.response.VariantListResponse;
import com.ptit.clone.entity.AttributeDefinition;
import com.ptit.clone.entity.Product;
import com.ptit.clone.entity.ProductVariant;
import com.ptit.clone.entity.ProductVariantOption;
import com.ptit.clone.mapper.ProductMapper;
import com.ptit.clone.mapper.VariantMapper;
import com.ptit.clone.messaging.event.CartVariantQueryEvent;
import com.ptit.clone.messaging.event.CartVariantQueryResponse;
import com.ptit.clone.messaging.event.VariantDeletedEvent;
import com.ptit.clone.messaging.event.VariantEnrichData;
import com.ptit.clone.messaging.event.VariantPausedEvent;
import com.ptit.clone.messaging.producer.ProductUpsertProducer;
import com.ptit.clone.messaging.producer.VariantDeletedProducer;
import com.ptit.clone.messaging.producer.VariantPausedProducer;
import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.model.AvailabilityStatus;
import com.ptit.clone.model.ProductStatus;
import com.ptit.clone.model.VariantStatus;
import com.ptit.clone.respository.IAttributeDefinitionRepository;
import com.ptit.clone.respository.IProductRepository;
import com.ptit.clone.respository.IProductVariantRepository;
import com.ptit.clone.service.IAdminVariantService;
import com.ptit.clone.service.IImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminVariantServiceIplm implements IAdminVariantService {

    private final IProductRepository productRepository;
    private final IProductVariantRepository variantRepository;
    private final IAttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductUpsertProducer producer;
    private final VariantPausedProducer variantPausedProducer;
    private final VariantDeletedProducer variantDeletedProducer;
    private final ProductMapper productMapper;
    private final VariantMapper variantMapper;
    private final IImageStorageService imageStorageService;

    @Override
    public void createVariant(UUID productId, CreateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        String sku = request.getSku().trim();
        String barcode = request.getBarcode() == null ? null : request.getBarcode().trim();

        if (variantRepository.existsBySkuIgnoreCase(sku)) {
            throw new IllegalArgumentException("SKU already exists: " + request.getSku());
        }
        if (barcode != null && !barcode.isBlank()
                && variantRepository.existsByBarcodeIgnoreCase(barcode))
            throw new IllegalArgumentException("Barcode already exists: " + barcode);
        if (request.getSalePrice() != null && request.getListPrice() != null
                && request.getSalePrice() > request.getListPrice()) {
            throw new IllegalArgumentException("Sale price must be less than or equal to list price");
        }

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(sku);
        variant.setBarcode(barcode);
        variant.setName(request.getName() != null ? request.getName().trim() : null);
        variant.setOptionValues(new java.util.LinkedHashSet<>());
        replaceVariantOptions(product, variant, request.getOptionValues());
        variant.setListPrice(request.getListPrice() != null ? request.getListPrice() : 0L);
        variant.setSalePrice(request.getSalePrice() != null ? request.getSalePrice() : 0L);
        variant.setAvailabilityStatus(request.getAvailabilityStatus() != null
                ? request.getAvailabilityStatus()
                : AvailabilityStatus.OUT_OF_STOCK);
        variant.setHeroImage(request.getHeroImage());
        variant.setImages(request.getImages() != null ? request.getImages() : new java.util.ArrayList<>());
        variant.setStatus(request.getStatus() != null ? request.getStatus() : VariantStatus.INACTIVE);

        product.getVariants().add(variant);
        variantRepository.save(variant);

        if (product.getStatus() == ProductStatus.ACTIVE && variant.getStatus() == VariantStatus.ACTIVE) {
            producer.fire(productMapper.toProductUpsertedEvent(product));
        }
    }

    @Override
    public void updateVariant(UUID productId, UUID variantId, UpdateVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));

        if (variant.getProduct() == null || !variant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Variant does not belong to product: " + productId);
        }

        if (request.getBarcode() != null && !request.getBarcode().isBlank()
                && variantRepository.existsByBarcodeIgnoreCaseAndIdNot(request.getBarcode(), variantId)) {
            throw new IllegalArgumentException("Barcode already exists: " + request.getBarcode());
        }

        if (request.getName() != null) variant.setName(request.getName().trim());
        if (request.getBarcode() != null) variant.setBarcode(request.getBarcode());
        if (request.getOptionValues() != null) {
            replaceVariantOptions(product, variant, request.getOptionValues());
        }
        if (request.getHeroImage() != null) variant.setHeroImage(request.getHeroImage());
        if (request.getImages() != null) variant.setImages(request.getImages());
        if (request.getStatus() != null) variant.setStatus(request.getStatus());
        if (request.getAvailabilityStatus() != null) variant.setAvailabilityStatus(request.getAvailabilityStatus());

        if (request.getListPrice() != null || request.getSalePrice() != null) {
            long listPrice = request.getListPrice() != null ? request.getListPrice() : variant.getListPrice();
            long salePrice = request.getSalePrice() != null ? request.getSalePrice() : variant.getSalePrice();
            if (salePrice > listPrice) {
                throw new IllegalArgumentException("Sale price must be less than or equal to list price");
            }
            variant.setListPrice(listPrice);
            variant.setSalePrice(salePrice);
        }

        ProductVariant saved = variantRepository.save(variant);

        if (product.getStatus() == ProductStatus.ACTIVE && variant.getStatus() == VariantStatus.ACTIVE) {
            producer.fire(productMapper.toProductUpsertedEvent(product));
        }
    }

    @Override
    public void pauseVariant(UUID productId, UUID variantId) {
        ProductVariant variant = loadVariantForProduct(productId, variantId);

        if (variant.getStatus() == VariantStatus.INACTIVE) {
            return;
        }

        variant.setStatus(VariantStatus.INACTIVE);
        variantRepository.save(variant);

        variantPausedProducer.fire(new VariantPausedEvent(productId, variantId));
    }

    @Override
    public void activateVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));

        if (variant.getProduct() == null || !variant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Variant does not belong to product: " + productId);
        }

        if (variant.getStatus() == VariantStatus.ACTIVE) {
            return;
        }

        variant.setStatus(VariantStatus.ACTIVE);
        variantRepository.save(variant);

        if (product.getStatus() == ProductStatus.ACTIVE) {
            producer.fire(productMapper.toProductUpsertedEvent(product));
        }
    }

    @Override
    public void deleteVariant(UUID productId, UUID variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));

        if (variant.getProduct() == null || !variant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Variant does not belong to product: " + productId);
        }

        imageStorageService.delete(variant.getHeroImage());
        if (variant.getImages() != null) {
            for (String image : variant.getImages()) {
                imageStorageService.delete(image);
            }
        }

        product.getVariants().remove(variant);
        variantRepository.delete(variant);

        variantDeletedProducer.fire(new VariantDeletedEvent(productId, variantId));
    }

    private ProductVariant loadVariantForProduct(UUID productId, UUID variantId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found: " + variantId));

        if (variant.getProduct() == null || !variant.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Variant does not belong to product: " + productId);
        }
        return variant;
    }

    @Override
    @Transactional(readOnly = true)
    public VariantListResponse getAllVariants(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        List<ProductVariantResponse> items = variantRepository.findAllByProductIdWithOptions(productId)
                .stream()
                .map(variantMapper::toProductVariantResponse)
                .toList();
        return VariantListResponse.builder().items(items).build();
    }

    @Override
    public CartVariantQueryResponse getVariantEnrichData(CartVariantQueryEvent request) {
        List<ProductVariant> variants = variantRepository.findAllByIdInWithProduct(request.getVariantIds());
        List<VariantEnrichData> enrichDataList = new ArrayList<>();
        for(ProductVariant variant : variants){
            enrichDataList.add(variantMapper.toVariantEnrichData(variant));
        }
        return new CartVariantQueryResponse(enrichDataList);

    }

    private void replaceVariantOptions(Product product, ProductVariant variant, Map<String, String> optionValues) {
        variant.getOptionValues().clear();
        if (optionValues == null || optionValues.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : optionValues.entrySet()) {
            AttributeDefinition definition = attributeDefinitionRepository.findByCodeIgnoreCase(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Attribute not found: " + entry.getKey()));
            if (definition.getScope() != AttributeScope.VARIANT) {
                throw new IllegalArgumentException("Attribute is not variant-scoped: " + definition.getCode());
            }
            if (definition.getProductType() != null
                    && product.getProductType() != null
                    && !definition.getProductType().getId().equals(product.getProductType().getId())) {
                throw new IllegalArgumentException("Attribute does not belong to product type: " + definition.getCode());
            }

            ProductVariantOption option = new ProductVariantOption();
            option.setVariant(variant);
            option.setAttributeDefinition(definition);
            option.setValue(entry.getValue());
            variant.getOptionValues().add(option);
        }
    }
}

