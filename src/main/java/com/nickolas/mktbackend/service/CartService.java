package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.model.Cart;
import com.nickolas.mktbackend.model.CartItem;
import com.nickolas.mktbackend.model.Product;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.CartItemRepository;
import com.nickolas.mktbackend.repository.CartRepository;
import com.nickolas.mktbackend.repository.ProductRepository;
import com.nickolas.mktbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//    private final JwtProvider jwtProvider;
//
//    public Cart getCart(String token) {
//        Long userId = jwtProvider.getUserIdFromToken(token, userRepository);
//        if (userId == null) {
//            throw new RuntimeException("Доступ заборонено. Увійдіть у свій акаунт.");
//        }
//        return cartRepository.findByUserId(userId)
//                .orElseGet(() -> createCartForUser(userRepository.findById(userId).orElseThrow()));
//    }
//
//
//    @Transactional
//    public void addToCart(String token, Long productId) {
//        Cart cart = getCart(token);
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
//
//        Optional<CartItem> existingItem = cart.getItems().stream()
//                .filter(item -> item.getProduct().getId().equals(productId))
//                .findFirst();
//
//        if (existingItem.isPresent()) {
//            CartItem item = existingItem.get();
//            if (item.getQuantity() >= product.getQuantity()) {
//                throw new RuntimeException("Це максимальна кількість товару на складі");
//            }
//        } else {
//            CartItem newItem = new CartItem();
//            newItem.setCart(cart);
//            newItem.setProduct(product);
//            newItem.setQuantity(1);
//            cart.getItems().add(newItem);
//        }
//
//        cartRepository.save(cart);
//    }
//
//    @Transactional
//    public void removeFromCart(String token, Long productId) {
//        Cart cart = getCart(token);
//        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
//        cartRepository.save(cart);
//    }
//
//    @Transactional
//    public void clearCart(String token) {
//        Cart cart = getCart(token);
//        cart.getItems().clear();
//        cartRepository.save(cart);
//    }
//
//
//
//    @Transactional
//    public void increaseQuantity(String token, Long productId) {
//        Cart cart = getCart(token);
//        CartItem item = cart.getItems().stream()
//                .filter(i -> i.getProduct().getId().equals(productId))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Цього товару немає у вашому кошику"));
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
//
//        if (item.getQuantity() >= product.getQuantity()) {
//            throw new RuntimeException("Товару більше нема на складі");
//        }
//
//        item.setQuantity(item.getQuantity() + 1);
//        cartRepository.save(cart);
//    }
//
//    private Cart createCartForUser(User user) {
//        Cart cart = new Cart();
//        cart.setUser(user);
//        cart.setItems(new ArrayList<>());
//        return cartRepository.save(cart);
//    }
private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Cart getCart(String token) {
        Long userId = jwtProvider.getUserIdFromToken(token, userRepository);
        if (userId == null) {
            throw new RuntimeException("Доступ заборонено. Увійдіть у свій акаунт.");
        }
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCartForUser(userRepository.findById(userId).orElseThrow()));
    }

    @Transactional
    public void addToCart(String token, Long productId) {
        Cart cart = getCart(token);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));

        if (product.getQuantity() <= 0) {
            throw new RuntimeException("Цей товар закінчився на складі!");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (item.getQuantity() >= product.getQuantity()) {
                throw new RuntimeException("Це максимальна кількість товару на складі");
            }
            item.setQuantity(item.getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cart.getItems().add(newItem);
        }

        // Зменшуємо кількість товару в базі
        product.setQuantity(product.getQuantity() - 1);
        productRepository.save(product);
        cartRepository.save(cart);
    }

    @Transactional
    public void increaseQuantity(String token, Long productId) {
        Cart cart = getCart(token);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Цього товару немає у вашому кошику"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));

        if (item.getQuantity() >= product.getQuantity()) {
            throw new RuntimeException("Товару більше нема на складі");
        }

        item.setQuantity(item.getQuantity() + 1);


        product.setQuantity(product.getQuantity() - 1);
        productRepository.save(product);
        cartRepository.save(cart);
    }

    @Transactional
    public void decreaseQuantity(String token, Long productId) {
        Cart cart = getCart(token);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Цього товару немає у вашому кошику"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);

            // Повертаємо товар у базу
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
            product.setQuantity(product.getQuantity() + 1);
            productRepository.save(product);
        } else {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(String token, Long productId) {
        Cart cart = getCart(token);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (item != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);

            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String token) {
        Cart cart = getCart(token);
        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Продукт не знайдено"));
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        return cartRepository.save(cart);
    }
}
