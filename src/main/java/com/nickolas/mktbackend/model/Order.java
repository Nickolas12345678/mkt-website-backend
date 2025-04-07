package com.nickolas.mktbackend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nickolas.mktbackend.domain.DeliveryMethod;
import com.nickolas.mktbackend.domain.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

}
