package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;


public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    Page<Product> getAllProducts(Pageable pageable);
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<Product> getProductsByFilters(String name, Long categoryId, Pageable pageable);
}
