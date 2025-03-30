package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.request.SigninRequest;
import com.nickolas.mktbackend.request.SignupRequest;
import com.nickolas.mktbackend.response.ApiResponse;
import com.nickolas.mktbackend.response.AuthResponse;
import com.nickolas.mktbackend.service.AuthService;
import com.nickolas.mktbackend.service.OTPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final OTPService otpService;




    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            return ResponseEntity.badRequest().body(errors);
        }

        try {
            User user = authService.registerUser(signupRequest);
            String token = authService.generateToken(user);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(token);
            authResponse.setMessage("Реєстрація успішна");
            authResponse.setRole(user.getRole().name());

            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest signinRequest) {

        try{
        AuthResponse authResponse = authService.authenticateUser(signinRequest);
                   authResponse.setMessage("Автентифікація успішна");
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }catch(RuntimeException e){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage(e.getMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping("/otp/generate")
//    public ResponseEntity<String> generateOtp(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        otpService.generateOtp(email);
//        return ResponseEntity.ok("OTP відправлено на email: " + email);
//    }
//
//    @PostMapping("/otp/verify")
//    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
//        String email = request.get("email");
//        String otp = request.get("otp");
//
//        boolean isValid = otpService.verifyOtp(email, otp);
//
//        if (isValid) {
//            return ResponseEntity.ok(Map.of("message", "OTP підтверджено"));
//        } else {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Неправильний OTP"));
//        }
//    }
}
