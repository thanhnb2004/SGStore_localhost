package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateBrandRequest;
import com.ptit.clone.dtos.request.UpdateBrandRequest;
import com.ptit.clone.dtos.response.BrandResponse;
import com.ptit.clone.dtos.response.BrandListResponse;
import com.ptit.clone.entity.Brand;
import com.ptit.clone.mapper.BrandMapper;
import com.ptit.clone.model.EntityStatus;
import com.ptit.clone.respository.IBrandRespository;
import com.ptit.clone.respository.IProductRepository;
import com.ptit.clone.service.IAdminBrandService;
import com.ptit.clone.service.IImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBrandServiceIplm implements IAdminBrandService {

    private final IBrandRespository brandRespository;
    private final IProductRepository productRepository;
    private final BrandMapper mapper;
    private final IImageStorageService imageStorageService;

    @Override
    public void createBrand(CreateBrandRequest request) {
        String slug = normalizeSlug(request.getName());

        if (brandRespository.findBySlugIgnoreCase(slug).isPresent()) {
            throw new IllegalArgumentException("Brand slug already exists: " + slug);
        }

        Brand brand = new Brand();
        brand.setSlug(slug);
        brand.setName(request.getName().trim());
        brand.setLogo(request.getLogo());
        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        } else {
            brand.setStatus(EntityStatus.INACTIVE);
        }
        brandRespository.save(brand);
    }

    @Override
    public BrandResponse getBrand(UUID brandId) {
        Brand brand = brandRespository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
        return mapper.toBrandResponse(brand);
    }

    @Override
    public void updateBrand(UUID brandId, UpdateBrandRequest request) {
        Brand brand = brandRespository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found: " + brandId));
        if (request.getName() != null && !request.getName().isBlank()) {
            brand.setName(request.getName().trim());
            brand.setSlug(normalizeSlug(request.getName()));
        }
        if (request.getLogo() != null) {
            String oldLogo = brand.getLogo();
            if (oldLogo != null && !oldLogo.equals(request.getLogo())) {
                imageStorageService.delete(oldLogo);
            }
            brand.setLogo(request.getLogo());
        }
        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        }
        brandRespository.save(brand);
    }

    @Override
    public BrandListResponse listBrands(EntityStatus status) {
        List<Brand> brands = brandRespository.findAll();
        List<BrandResponse> items = new ArrayList<>();
        for (Brand brand : brands) {
            BrandResponse response = mapper.toBrandResponse(brand);
            items.add(response);
        }
        BrandListResponse result = BrandListResponse.builder()
                .items(items)
                .build();
        return result;
    }

    @Override
    public void deleteBrands(List<UUID> brandIds) {
        List<UUID> normalizedBrandIds = normalizeIds(brandIds, "Brand");
        if (productRepository.existsByBrand_IdIn(normalizedBrandIds)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete brands that are assigned to products");
        }
        List<Brand> brands = brandRespository.findAllById(normalizedBrandIds);
        for (Brand brand : brands) {
            imageStorageService.delete(brand.getLogo());
        }
        brandRespository.deleteAllByIdInBatch(normalizedBrandIds);
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
