package com.nickolas.mktbackend.repository;

import com.nickolas.mktbackend.model.Order;
import com.nickolas.mktbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findAllByUser(User user);
}
