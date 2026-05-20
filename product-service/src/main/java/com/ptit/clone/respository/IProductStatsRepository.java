package com.ptit.clone.respository;

import com.ptit.clone.entity.ProductStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IProductStatsRepository extends JpaRepository<ProductStats, UUID> {
    Optional<ProductStats> findByProductId(UUID productId);
}
