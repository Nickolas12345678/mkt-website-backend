package com.nickolas.mktbackend.repository;

import com.nickolas.mktbackend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
