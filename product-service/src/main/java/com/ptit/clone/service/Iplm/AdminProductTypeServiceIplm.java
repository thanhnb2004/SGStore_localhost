package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateProductTypeRequest;
import com.ptit.clone.dtos.response.ProductTypeResponse;
import com.ptit.clone.dtos.response.ProductTypeListResponse;
import com.ptit.clone.entity.ProductType;
import com.ptit.clone.mapper.ProductTypeMapper;
import com.ptit.clone.respository.IAttributeDefinitionRepository;
import com.ptit.clone.respository.IProductRepository;
import com.ptit.clone.respository.IProductTypeRepository;
import com.ptit.clone.service.IAdminProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminProductTypeServiceIplm implements IAdminProductTypeService {
    private final IProductTypeRepository productTypeRepository;
    private final IProductRepository productRepository;
    private final IAttributeDefinitionRepository attributeDefinitionRepository;
    private final ProductTypeMapper mapper;

    @Override
    public void createProductType(CreateProductTypeRequest request) {
        String slug = normalizeSlug(request.getName());

        if (productTypeRepository.findBySlugIgnoreCase(slug).isPresent()) {
            throw new IllegalArgumentException("ProductType slug already exists: " + slug);
        }

        ProductType productType = ProductType.builder()
                .name(request.getName().trim())
                .slug(slug)
                .build();
        productTypeRepository.save(productType);
    }

    @Override
    public ProductTypeListResponse listProductTypes() {
        List<ProductType> productTypes = productTypeRepository.findAll();
        List<ProductTypeResponse> items = new ArrayList<>();
        for (ProductType productType : productTypes) {
            ProductTypeResponse response = mapper.toProductTypeDetailResponse(productType);
            items.add(response);
        }
        return ProductTypeListResponse.builder()
                .items(items)
                .build();
    }

    @Override
    public void deleteProductTypes(List<UUID> productTypeIds) {
        List<UUID> normalizedProductTypeIds = normalizeIds(productTypeIds, "ProductType");
        if (productRepository.existsByProductType_IdIn(normalizedProductTypeIds)) {
            throw new IllegalArgumentException("Cannot delete product types that are assigned to products");
        }
        attributeDefinitionRepository.deleteAllByProductType_IdIn(normalizedProductTypeIds);
        productTypeRepository.deleteAllByIdInBatch(normalizedProductTypeIds);
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

    private List<UUID> normalizeIds(List<UUID> ids, String entityName) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(entityName + " ids must not be empty");
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }

}
