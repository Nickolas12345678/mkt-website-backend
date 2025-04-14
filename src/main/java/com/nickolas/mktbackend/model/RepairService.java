package com.nickolas.mktbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "repair_services")
@Data
public class RepairService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;
}
