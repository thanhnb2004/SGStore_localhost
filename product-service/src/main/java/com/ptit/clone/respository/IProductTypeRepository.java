package com.ptit.clone.respository;

import com.ptit.clone.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductTypeRepository extends JpaRepository<ProductType, UUID> {
    Optional<ProductType> findBySlugIgnoreCase(String slug);
}
