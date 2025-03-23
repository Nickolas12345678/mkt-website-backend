package com.nickolas.mktbackend.model;

import com.nickolas.mktbackend.domain.ServiceRequestStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_requests")
@Data
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    private String description;

    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;
}
