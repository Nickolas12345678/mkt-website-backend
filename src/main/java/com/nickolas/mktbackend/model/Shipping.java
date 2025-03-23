package com.nickolas.mktbackend.model;

import com.nickolas.mktbackend.domain.ShippingStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "shippings")
@Data
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    private String shippingMethod;

    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;

    private LocalDateTime estimatedDeliveryDate;
}
