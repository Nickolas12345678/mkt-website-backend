package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.model.Product;
import com.nickolas.mktbackend.model.User;

import java.util.List;
import com.nickolas.mktbackend.model.Seller;

public interface ProductService {
    Product addProduct(Product product, Seller seller);
    Product updateProduct(Long id, Product productDetails, Seller seller);
    void deleteProduct(Long id, Seller seller);
    List<Product> getProductsBySeller(Long sellerId);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(Long categoryId);
}
