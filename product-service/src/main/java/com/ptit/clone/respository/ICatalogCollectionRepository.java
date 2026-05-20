package com.ptit.clone.respository;

import com.ptit.clone.entity.ProductCollection;
import com.ptit.clone.model.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICatalogCollectionRepository extends JpaRepository<ProductCollection, UUID> {
    Optional<ProductCollection> findBySlugIgnoreCase(String slug);

    List<ProductCollection> findAllByStatusOrderBySortOrderAsc(EntityStatus status);
}
