package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.exception.SellerException;
import com.nickolas.mktbackend.model.Seller;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.SellerRepository;
import com.nickolas.mktbackend.repository.UserRepository;
import com.nickolas.mktbackend.request.LoginRequest;
import com.nickolas.mktbackend.response.AuthResponse;
import com.nickolas.mktbackend.response.SellerResponse;
import com.nickolas.mktbackend.service.OTPService;
import com.nickolas.mktbackend.service.SellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sellers")
@RequiredArgsConstructor
@EnableMethodSecurity
public class SellerController {
    private final SellerService sellerService;
    private final UserRepository userRepository;
    private final OTPService otpService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final SellerRepository sellerRepository;


    @PostMapping("/register")
    public ResponseEntity<?> registerSeller(@Valid @RequestBody Seller seller) {
        if (userRepository.findByEmail(seller.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Користувач з таким email вже зареєстрований."));
        }
        if (sellerRepository.findByEmail(seller.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Продавець з таким email вже існує."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerService.createSeller(seller));
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginSeller(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Користувача з таким email не знайдено."));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Невірний пароль."));
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority( user.getRole().name()))
        );

        String token = jwtProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(token, user.getRole().name(), "Авторизація успішна."));
    }


    @PostMapping("/generate-otp")
    public ResponseEntity<?> generateOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email є обов'язковим полем."));
        }

        Seller seller = sellerRepository.findByEmail(email);

        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Продавця з таким email не знайдено."));
        }

        otpService.generateOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP-код відправлено на email."));
    }


    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || email.isEmpty() || otp == null || otp.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email та OTP є обов'язковими полями."));
        }

        try {
            Seller seller = sellerService.verifyEmail(email, otp);
            seller.setEmailVerified(true);
            sellerRepository.save(seller);
            return ResponseEntity.ok(Map.of("message", "Email успішно підтверджено."));
        } catch (SellerException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getSellerById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(sellerService.getSellerById(id));
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Seller>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeller(@PathVariable("id") Long id, @Valid @RequestBody Seller seller) {
        try {
            return ResponseEntity.ok(sellerService.updateSeller(id, seller));
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeller(@PathVariable("id") Long id) {
        try {
            sellerService.deleteSeller(id);
            return ResponseEntity.ok(Map.of("message", "Продавця успішно видалено"));
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/email")
    public ResponseEntity<?> getSellerByEmail(@RequestParam String email) {
        try {
            return ResponseEntity.ok(sellerService.getSellerByEmail(email));
        } catch (SellerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(
            @RequestHeader("Authorization") String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromToken(jwt);
        Seller seller = sellerService.getSellerByEmail(email);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

}
