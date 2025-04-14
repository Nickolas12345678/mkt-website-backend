package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.domain.DeliveryMethod;
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

        if (orderRequest.getDeliveryMethod() == null) {
            throw new RuntimeException("Delivery method is required");
        }

        if (orderRequest.getDeliveryMethod().name().equals("COURIER")) {
            if (orderRequest.getDeliveryAddress() == null || orderRequest.getDeliveryAddress().isBlank()) {
                throw new RuntimeException("Delivery address is required for courier delivery");
            }
        } else if (orderRequest.getDeliveryMethod().name().equals("PICKUP")) {
            List<String> allowedPickupAddresses = List.of(
                    "Ужгород, вул. Собранецька, 14",
                    "Ужгород, вул. Героїв 101-ї бригади, 9"
            );

            String normalizedDeliveryAddress = orderRequest.getDeliveryAddress()
                    .toLowerCase()
                    .replaceAll("\\s*,\\s*", ",");

            boolean isValidAddress = allowedPickupAddresses.stream()
                    .map(address -> address.toLowerCase().replaceAll("\\s*,\\s*", ","))
                    .anyMatch(normalizedDeliveryAddress::equals);

            if (!isValidAddress) {
                throw new RuntimeException("Invalid pickup address");
            }
        }

        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setDeliveryMethod(orderRequest.getDeliveryMethod());
        newOrder.setDeliveryAddress(orderRequest.getDeliveryAddress());

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
    public void updateOrderStatus(String userEmail, Long orderId, OrderStatus newStatus) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isAdmin) {
            throw new RuntimeException("Only admin can update order status");
        }

        OrderStatus oldStatus = order.getStatus();

        if (!isValidStatusTransition(oldStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition: " + oldStatus + " → " + newStatus);
        }

        if (shouldDecreaseStock(oldStatus, newStatus)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int quantity = item.getQuantity();

                if (product.getQuantity() < quantity) {
                    throw new IllegalStateException("Not enough stock for product: " + product.getName());
                }

                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);
            }
        }

        if (shouldRestoreStock(oldStatus, newStatus)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int quantity = item.getQuantity();

                product.setQuantity(product.getQuantity() + quantity);
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }


    private boolean shouldDecreaseStock(OrderStatus oldStatus, OrderStatus newStatus) {
        return !wasStockDecreased(oldStatus) && isDecreasingStatus(newStatus);
    }

    private boolean shouldRestoreStock(OrderStatus oldStatus, OrderStatus newStatus) {
        return wasStockDecreased(oldStatus) && newStatus == OrderStatus.CANCELED;
    }

    private boolean isDecreasingStatus(OrderStatus status) {
        return status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED || status == OrderStatus.PICKUP_READY;
    }

    private boolean wasStockDecreased(OrderStatus status) {
        return isDecreasingStatus(status);
    }





    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELED || newStatus == OrderStatus.PICKUP_READY;
            case PICKUP_READY -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.PENDING || newStatus == OrderStatus.CANCELED;
            case DELIVERED, CANCELED -> false;
        };
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

        OrderStatus oldStatus = order.getStatus();

        if (wasStockDecreased(oldStatus)) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int quantity = item.getQuantity();

                product.setQuantity(product.getQuantity() + quantity);
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }



}
