package com.ptit.clone.respository;

import com.ptit.clone.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, UUID> {
    Optional<CartItem> findByCartIdAndVariantId(UUID cartId, UUID variantId);
}
