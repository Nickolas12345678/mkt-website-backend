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
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "repair_service_id")
    private RepairService repairService;

    private String description;

    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status;
}
