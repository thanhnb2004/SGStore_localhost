package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateCollectionRequest;
import com.ptit.clone.dtos.request.UpdateCollectionRequest;
import com.ptit.clone.dtos.response.CollectionResponse;
import com.ptit.clone.dtos.response.CollectionListResponse;
import com.ptit.clone.entity.ProductCollection;
import com.ptit.clone.mapper.CollectionMapper;
import com.ptit.clone.respository.ICatalogCollectionRepository;
import com.ptit.clone.respository.IProductRepository;
import com.ptit.clone.service.IAdminCollectionService;
import com.ptit.clone.service.IImageStorageService;
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
public class AdminCollectionServiceIplm implements IAdminCollectionService {

    private final ICatalogCollectionRepository collectionRepository;
    private final IProductRepository productRepository;
    private final CollectionMapper mapper;
    private final IImageStorageService imageStorageService;

    @Override
    public void createCollection(CreateCollectionRequest request) {
        String slug = normalizeSlug(request.getName());

        if (collectionRepository.findBySlugIgnoreCase(slug).isPresent()) {
            throw new IllegalArgumentException("Collection slug already exists: " + slug);
        }

        ProductCollection collection = new ProductCollection();
        collection.setSlug(slug);
        collection.setName(request.getName().trim());
        collection.setDescriptionHtml(request.getDescriptionHtml());
        collection.setStatus(request.getStatus() != null ? request.getStatus() : collection.getStatus());
        collection.setHeroBanner(request.getHeroBanner() != null ? request.getHeroBanner() : null);
        collection.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        collection.setFacetCodes(request.getFacetCodes() != null ? new ArrayList<>(request.getFacetCodes()) : new ArrayList<>());

        ProductCollection saved = collectionRepository.save(collection);
    }

    @Override
    public void updateCollection(UUID collectionId, UpdateCollectionRequest request) {
        ProductCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection not found: " + collectionId));

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String newSlug = normalizeSlug(request.getSlug());
            collectionRepository.findBySlugIgnoreCase(newSlug)
                    .filter(c -> !c.getId().equals(collectionId))
                    .ifPresent(c -> { throw new IllegalArgumentException("Collection slug already exists: " + newSlug); });
            collection.setSlug(newSlug);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            collection.setName(request.getName().trim());
        }
        if (request.getDescriptionHtml() != null) {
            collection.setDescriptionHtml(request.getDescriptionHtml());
        }
        if (request.getStatus() != null) {
            collection.setStatus(request.getStatus());
        }
        if (request.getSortOrder() != null) {
            collection.setSortOrder(request.getSortOrder());
        }
        if (request.getHeroBanner() != null) {
            collection.setHeroBanner(request.getHeroBanner());
        }
        if (request.getFacetCodes() != null) {
            collection.setFacetCodes(new ArrayList<>(request.getFacetCodes()));
        }
        ProductCollection saved = collectionRepository.save(collection);
    }

    @Override
    public CollectionListResponse listCollections() {
        List<ProductCollection> collections = collectionRepository.findAll();
        List<CollectionResponse> collectionDetailResponses = new ArrayList<>();
        for(ProductCollection collection : collections){
            collectionDetailResponses.add(mapper.toCollectionResponse(collection));
        }
        return CollectionListResponse.builder()
                .items(collectionDetailResponses)
                .build();
    }

    @Override
    public void deleteCollections(List<UUID> collectionIds) {
        List<UUID> normalizedCollectionIds = normalizeIds(collectionIds, "Collection");
        if (productRepository.existsByCollections_IdIn(normalizedCollectionIds)) {
            throw new IllegalArgumentException("Cannot delete collections that are assigned to products");
        }
        List<ProductCollection> collections = collectionRepository.findAllById(normalizedCollectionIds);
        for (ProductCollection collection : collections) {
            imageStorageService.delete(collection.getHeroBanner());
        }
        collectionRepository.deleteAllByIdInBatch(normalizedCollectionIds);
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
