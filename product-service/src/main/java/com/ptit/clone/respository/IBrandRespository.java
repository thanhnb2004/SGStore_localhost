package com.ptit.clone.respository;

import com.ptit.clone.entity.Brand;
import com.ptit.clone.model.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBrandRespository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findBySlugIgnoreCase(String slug);

    List<Brand> findAllByStatus(EntityStatus status);
}
