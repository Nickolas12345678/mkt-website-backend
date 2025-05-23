package com.nickolas.mktbackend.controller;


import com.nickolas.mktbackend.model.Cart;
import com.nickolas.mktbackend.model.CartItem;
import com.nickolas.mktbackend.request.CartItemRequest;
import com.nickolas.mktbackend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;



    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader(value = "Authorization", required = true) String token) {
        try {
            Cart cart = cartService.getCart(token);
            for (CartItem cartItem : cart.getItems()) {
                String imageURL = cartItem.getProduct().getImageURL();
                if (imageURL != null && !imageURL.isEmpty()) {
                    cartItem.getProduct().setImageURL("http://localhost:8080/images/" + imageURL);
                } else {
                    cartItem.getProduct().setImageURL("http://localhost:8080/images/default-image.jpg");
                }
            }
            return ResponseEntity.ok(cart);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestHeader(value = "Authorization", required = true) String token,
            @RequestBody CartItemRequest request) {
        try {
            cartService.addToCart(token, request.getProductId());
            return ResponseEntity.ok("Товар успішно додано до кошика!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/increase/{productId}")
    public ResponseEntity<String> increaseQuantity(
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable("productId") Long productId) {
        try {
            cartService.increaseQuantity(token, productId);
            return ResponseEntity.ok("Кількість товару збільшено.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/decrease/{productId}")
    public ResponseEntity<String> decreaseQuantity(
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable("productId") Long productId) {
        try {
            cartService.decreaseQuantity(token, productId);
            return ResponseEntity.ok("Кількість товару зменшено.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable("productId") Long productId) {
        try {
            cartService.removeFromCart(token, productId);
            return ResponseEntity.ok("Товар видалено з кошика.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader(value = "Authorization", required = true) String token) {
        try {
            cartService.clearCart(token);
            return ResponseEntity.ok("Кошик очищено.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
