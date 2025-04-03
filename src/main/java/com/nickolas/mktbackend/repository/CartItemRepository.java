package com.nickolas.mktbackend.repository;

import com.nickolas.mktbackend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
