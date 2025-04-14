package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.domain.OrderStatus;
import com.nickolas.mktbackend.model.Order;
import com.nickolas.mktbackend.request.CreateOrderRequest;
import com.nickolas.mktbackend.request.UpdateOrderStatusRequest;
import com.nickolas.mktbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtProvider jwtProvider;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Object> getAllOrders(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            return ResponseEntity.ok(orderService.getAllOrders(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<Object> getOrdersForUser(
            @RequestHeader("Authorization") String token) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            return ResponseEntity.ok(orderService.getOrdersForUser(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderRequest orderRequest) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            orderService.createOrder(email, orderRequest);
            return ResponseEntity.ok("Замовлення успішно створено!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(
            @RequestHeader("Authorization") String token,
            @PathVariable("orderId") Long orderId) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            return ResponseEntity.ok(orderService.getOrderDetails(email, orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable("orderId") Long orderId,
            @RequestBody UpdateOrderStatusRequest statusRequest) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            orderService.updateOrderStatus(email, orderId, statusRequest.getStatus());
            return ResponseEntity.ok("Order status updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable("orderId") Long orderId) {
        try {
            String email = jwtProvider.getEmailFromToken(token);
            orderService.cancelOrder(email, orderId);
            return ResponseEntity.ok("Замовлення скасовано.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
