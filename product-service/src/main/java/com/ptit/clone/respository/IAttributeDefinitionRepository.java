package com.ptit.clone.respository;

import com.ptit.clone.entity.AttributeDefinition;
import com.ptit.clone.model.AttributeScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAttributeDefinitionRepository extends JpaRepository<AttributeDefinition, UUID> {
    Optional<AttributeDefinition> findByCodeIgnoreCase(String code);

    List<AttributeDefinition> findAllByOrderByFilterPositionAsc();

    List<AttributeDefinition> findAllByProductType_IdOrderByFilterPositionAsc(UUID productTypeId);

    List<AttributeDefinition> findAllByProductType_IdAndScopeOrderByFilterPositionAsc(UUID productTypeId, AttributeScope scope);

    boolean existsByProductType_IdIn(Collection<UUID> productTypeIds);

    void deleteAllByProductType_IdIn(Collection<UUID> productTypeIds);
}
