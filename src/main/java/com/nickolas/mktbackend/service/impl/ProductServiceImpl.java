package com.nickolas.mktbackend.service.impl;

import com.nickolas.mktbackend.model.Product;
import com.nickolas.mktbackend.model.Seller;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.ProductRepository;
import com.nickolas.mktbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product addProduct(Product product, Seller seller) {
        product.setSeller(seller);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product productDetails, Seller seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("У вас немає прав на зміну цього товару");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setQuantity(productDetails.getQuantity());
        product.setImageURL(productDetails.getImageURL());
        product.setCategory(productDetails.getCategory());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id, Seller seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("У вас немає прав на видалення цього товару");
        }

        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

}
