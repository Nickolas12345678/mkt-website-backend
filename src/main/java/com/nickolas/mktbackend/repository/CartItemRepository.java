package com.nickolas.mktbackend.repository;

import com.nickolas.mktbackend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.product.id = :productId")
    int sumQuantityByProductId(@Param("productId") Long productId);

}
