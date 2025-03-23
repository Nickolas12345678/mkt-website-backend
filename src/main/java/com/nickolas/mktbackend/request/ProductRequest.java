package com.nickolas.mktbackend.request;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String imageURL;
    private Long categoryId;
    private Long sellerId;
}
