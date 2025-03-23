package com.nickolas.mktbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "service_request_items")
@Data
public class ServiceRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_request_id", referencedColumnName = "id")
    private ServiceRequest serviceRequest;

    @ManyToOne
    @JoinColumn(name = "repair_service_id", referencedColumnName = "id")
    private RepairService repairService;

    private Double price;
}
