package com.ptit.clone.respository;

import com.ptit.clone.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    @Query("SELECT v FROM ProductVariant v JOIN FETCH v.product WHERE v.id IN :ids")
    List<ProductVariant> findAllByIdInWithProduct(@Param("ids") List<UUID> ids);

    @Query("SELECT v FROM ProductVariant v LEFT JOIN FETCH v.optionValues o LEFT JOIN FETCH o.attributeDefinition WHERE v.product.id = :productId")
    List<ProductVariant> findAllByProductIdWithOptions(@Param("productId") UUID productId);
    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByBarcodeIgnoreCase(String barcode);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, UUID id);

    boolean existsByBarcodeIgnoreCaseAndIdNot(String barcode, UUID id);

    boolean existsByOptionValues_AttributeDefinition_IdIn(Collection<UUID> attributeIds);
}
