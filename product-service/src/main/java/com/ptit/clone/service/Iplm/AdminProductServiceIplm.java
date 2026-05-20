package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateProductRequest;
import com.ptit.clone.dtos.request.PublishProductRequest;
import com.ptit.clone.dtos.request.UpdateProductRequest;
import com.ptit.clone.dtos.response.ProductDetailResponse;
import com.ptit.clone.dtos.response.ProductListResponse;
import com.ptit.clone.dtos.response.ProductResponse;
import com.ptit.clone.entity.*;
import com.ptit.clone.mapper.ProductMapper;
import com.ptit.clone.messaging.event.ProductArchivedEvent;
import com.ptit.clone.messaging.event.ProductDeletedEvent;
import com.ptit.clone.messaging.producer.ProductArchivedProducer;
import com.ptit.clone.messaging.producer.ProductDeletedProducer;
import com.ptit.clone.messaging.producer.ProductUpsertProducer;
import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.model.ProductStatus;
import com.ptit.clone.model.VariantStatus;
import com.ptit.clone.respository.*;
import com.ptit.clone.service.IAdminProductService;
import com.ptit.clone.service.IAdminVariantService;
import com.ptit.clone.service.IImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductServiceIplm implements IAdminProductService {

    private final IBrandRespository brandRespository;
    private final IProductTypeRepository productTypeRepository;
    private final ICatalogCollectionRepository catalogCollectionRepository;
    private final IAttributeDefinitionRepository attributeDefinitionRepository;
    private final IProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductUpsertProducer producer;
    private final ProductArchivedProducer productArchivedProducer;
    private final ProductDeletedProducer productDeletedProducer;
    private final IImageStorageService imageStorageService;
    private final IAdminVariantService adminVariantService;

    @Override
    public void createProduct(CreateProductRequest request) {
        String slug = normalizeSlug(request.getName());

        if (productRepository.findBySlugIgnoreCase(slug).isPresent()) {
            throw new IllegalArgumentException("Product slug already exists: " + slug);
        }

        Product product = new Product();
        product.setSlug(slug);
        product.setName(request.getName().trim());
        product.setShortDescription(request.getShortDescription());
        product.setDescriptionHtml(request.getDescriptionHtml());
        product.setBrand(brandRespository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + request.getBrandId())));
        product.setProductType(productTypeRepository.findById(request.getProductTypeId())
                .orElseThrow(() -> new IllegalArgumentException("ProductType not found: " + request.getProductTypeId())));
        product.setStatus(request.getStatus() != null ? request.getStatus() : ProductStatus.DRAFT);
        product.setHeroImage(request.getHeroImage());
        ensureProductStats(product);
        product.setCollections(resolveCollections(request.getCollectionIds()));
        replaceProductAttributes(product, request.getProductAttributes());
        if (product.getStatus() == ProductStatus.ACTIVE) {
            product.setPublishedAt(LocalDateTime.now());
        }
        Product saved = productRepository.save(product);
        adminVariantService.createVariant(saved.getId(), request.getDefaultVariant());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductListResponse getAllProducts() {
        List<ProductResponse> items = productRepository.findAll().stream()
                .map(productMapper::toProductResponse)
                .toList();
        return ProductListResponse.builder()
                .items(items)
                .build();
    }

    @Override
    public ProductDetailResponse getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        return productMapper.toProductDetailResponse(product);
    }

    @Override
    public void updateProduct(UUID productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ensureProductStats(product);

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String newSlug = normalizeSlug(request.getSlug());
            productRepository.findBySlugIgnoreCase(newSlug)
                    .filter(p -> !p.getId().equals(productId))
                    .ifPresent(p -> { throw new IllegalArgumentException("Product slug already exists: " + newSlug); });
            product.setSlug(newSlug);
        }
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getName() != null) product.setName(request.getName().trim());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getDescriptionHtml() != null) product.setDescriptionHtml(request.getDescriptionHtml());
        if (request.getHeroImage() != null) product.setHeroImage(request.getHeroImage());
        if (request.getBrandId() != null) {
            product.setBrand(brandRespository.findById(request.getBrandId())
                    .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + request.getBrandId())));
        }
        if (request.getProductTypeId() != null) {
            product.setProductType(productTypeRepository.findById(request.getProductTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("ProductType not found: " + request.getProductTypeId())));
        }
        if (request.getCollectionIds() != null) {
            product.setCollections(resolveCollections(request.getCollectionIds()));
        }
        if (request.getProductAttributes() != null) {
            replaceProductAttributes(product, request.getProductAttributes());
        }

        Product saved = productRepository.save(product);
        if (saved.getStatus() == ProductStatus.ACTIVE) {
            producer.fire(productMapper.toProductUpsertedEvent(saved));
        }
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        imageStorageService.delete(product.getHeroImage());
        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                imageStorageService.delete(variant.getHeroImage());
                if (variant.getImages() != null) {
                    for (String image : variant.getImages()) {
                        imageStorageService.delete(image);
                    }
                }
            }
        }
        product.getCollections().clear();
        productRepository.delete(product);

        productDeletedProducer.fire(new ProductDeletedEvent(productId));
    }

    @Override
    public void archiveProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ensureProductStats(product);
        product.setStatus(ProductStatus.ARCHIVED);
        product.setPublishedAt(null);
        productRepository.save(product);

        productArchivedProducer.fire(new ProductArchivedEvent(productId));
    }

    @Override
    public void publishProduct(UUID productId, PublishProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        ensureProductStats(product);

        if (request.getPublished()) {
//            if (product.getHeroImage() == null || product.getHeroImage().isBlank()) {
//                throw new IllegalArgumentException("Product must have a hero image to publish");
//            }
            if (product.getCollections().isEmpty()) {
                throw new IllegalArgumentException("Product must belong to at least one collection to publish");
            }

            boolean hasActiveVariant = false;

            for (ProductVariant v : product.getVariants()) {
                if (v.getStatus() == VariantStatus.ACTIVE) {
                    hasActiveVariant = true;
                    break;
                }
            }

            if (!hasActiveVariant) {
                throw new IllegalArgumentException("Product must have at least one active variant to publish");
            }
            product.setStatus(ProductStatus.ACTIVE);
            product.setPublishedAt(LocalDateTime.now());
        } else {
            product.setStatus(ProductStatus.INACTIVE);
            product.setPublishedAt(null);
        }

        Product saved = productRepository.save(product);
        producer.fire(productMapper.toProductUpsertedEvent(saved));
    }

    private void replaceProductAttributes(Product product, Map<String, String> productAttributes) {
        product.getAttributes().clear();
        if (productAttributes == null || productAttributes.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : productAttributes.entrySet()) {
            AttributeDefinition definition = attributeDefinitionRepository.findByCodeIgnoreCase(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Attribute not found: " + entry.getKey()));
            if (definition.getScope() != AttributeScope.PRODUCT) {
                throw new IllegalArgumentException("Attribute is not product-scoped: " + definition.getCode());
            }
            if (definition.getProductType() != null
                    && product.getProductType() != null
                    && !definition.getProductType().getId().equals(product.getProductType().getId())) {
                throw new IllegalArgumentException("Attribute does not belong to product type: " + definition.getCode());
            }

            ProductAttributeValue value = new ProductAttributeValue();
            value.setProduct(product);
            value.setAttributeDefinition(definition);
            value.setValue(entry.getValue());
            product.getAttributes().add(value);
        }
    }

    private Set<ProductCollection> resolveCollections(Set<UUID> collectionIds) {
        if (collectionIds == null || collectionIds.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<ProductCollection> collections = new LinkedHashSet<>();
        for (UUID collectionId : collectionIds) {
            collections.add(catalogCollectionRepository.findById(collectionId)
                    .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId)));
        }
        return collections;
    }

    private void ensureProductStats(Product product) {
        if (product.getStats() != null) {
            return;
        }
        ProductStats stats = new ProductStats();
        stats.setProduct(product);
        stats.setRatingAverage(0.0);
        stats.setRatingCount(0);
        stats.setSoldCount(0);
        product.setStats(stats);
    }

    private String normalizeSlug(String value) {
        if (value == null) throw new IllegalArgumentException("Name must not be null");
        String slug = value.trim().toLowerCase(Locale.ROOT);
        slug = slug.replace("đ", "d");
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("(^-+)|(-+$)", "");
        return slug;
    }
}
