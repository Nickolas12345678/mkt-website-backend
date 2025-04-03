package com.nickolas.mktbackend.request;

import lombok.Data;

@Data
public class ProductRequestParams {
    private int page = 0;
    private int size = 12;
    private String name;
    private Long categoryId;
    private String sortOrder = "";
}
