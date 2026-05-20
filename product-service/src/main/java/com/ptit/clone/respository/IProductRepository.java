package com.ptit.clone.respository;

import com.ptit.clone.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = {"brand", "productType", "collections", "variants", "variants.optionValues", "variants.optionValues.attributeDefinition", "attributes", "attributes.attributeDefinition", "stats"})
    Optional<Product> findBySlugIgnoreCase(String slug);

    @EntityGraph(attributePaths = {"brand", "productType", "collections", "variants", "variants.optionValues", "variants.optionValues.attributeDefinition", "attributes", "attributes.attributeDefinition", "stats"})
    Optional<Product> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"brand", "productType", "collections", "variants", "variants.optionValues", "variants.optionValues.attributeDefinition", "attributes", "attributes.attributeDefinition", "stats"})
    List<Product> findAll();

    boolean existsByBrand_IdIn(Collection<UUID> brandIds);

    boolean existsByProductType_IdIn(Collection<UUID> productTypeIds);

    boolean existsByCollections_IdIn(Collection<UUID> collectionIds);

    boolean existsByAttributes_AttributeDefinition_IdIn(Collection<UUID> attributeIds);
}
