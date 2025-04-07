package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.domain.OrderStatus;
import com.nickolas.mktbackend.domain.Role;
import com.nickolas.mktbackend.model.*;
import com.nickolas.mktbackend.repository.*;
import com.nickolas.mktbackend.request.CreateOrderRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(String userEmail, CreateOrderRequest orderRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setDeliveryAddress(orderRequest.getDeliveryAddress());
        newOrder.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice());
            return item;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    @Transactional
    public List<Order> getAllOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }

        return orderRepository.findAll();
    }


    @Transactional
    public List<Order> getOrdersForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return orderRepository.findAllByUser(user);
    }

    @Transactional
    public Order getOrderDetails(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isAdmin && !order.getUser().equals(user)) {
            throw new RuntimeException("Order doesn't belong to the user");
        }

        return order;
    }



    @Transactional
    public void cancelOrder(String userEmail, Long orderId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getUser().equals(user))
                .orElseThrow(() -> new RuntimeException("Order not found or doesn't belong to the user"));

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

}
