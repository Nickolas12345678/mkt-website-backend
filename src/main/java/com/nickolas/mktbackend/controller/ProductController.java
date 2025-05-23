package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.domain.Role;
import com.nickolas.mktbackend.exception.ProductException;
import com.nickolas.mktbackend.model.Product;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.CategoryRepository;
import com.nickolas.mktbackend.repository.ProductRepository;
import com.nickolas.mktbackend.repository.SellerRepository;
import com.nickolas.mktbackend.request.DeleteProductRequest;
import com.nickolas.mktbackend.request.ProductRequest;
import com.nickolas.mktbackend.request.ProductRequestParams;
import com.nickolas.mktbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.nickolas.mktbackend.model.Category;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        try {
            if (productRequest.getPrice() <= 0 || productRequest.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Ціна та кількість повинні бути більші за нуль");
            }

            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Product product = new Product();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setQuantity(productRequest.getQuantity());
            product.setImageURL(productRequest.getImageURL());
            product.setCategory(category);

            Product savedProduct = productRepository.save(product);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setQuantity(productRequest.getQuantity());
        existingProduct.setImageURL(productRequest.getImageURL());
        existingProduct.setCategory(category);

        Product updatedProduct = productRepository.save(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Товар не знайдено", HttpStatus.NOT_FOUND));

        productRepository.delete(product);
        return ResponseEntity.ok("Товар видалено");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(ProductRequestParams params) {
        String sortBy = "id";
        if (params.getSortOrder().equalsIgnoreCase("asc") || params.getSortOrder().equalsIgnoreCase("desc")) {
            sortBy = "price";
        }
        Sort.Direction direction = params.getSortOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(params.getPage(), params.getSize(), Sort.by(direction, sortBy));
        Page<Product> products = productService.getProductsByFilters(params.getName(), params.getCategoryId(), pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsByCategory(@PathVariable("categoryId") Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }
}
