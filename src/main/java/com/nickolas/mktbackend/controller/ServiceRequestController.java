package com.nickolas.mktbackend.controller;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.domain.ServiceRequestStatus;
import com.nickolas.mktbackend.model.ServiceRequest;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.ProductRepository;
import com.nickolas.mktbackend.repository.ServiceRequestRepository;
import com.nickolas.mktbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {
    @Autowired
    private ServiceRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping
    public ServiceRequest createRequest(@RequestBody ServiceRequest request, @RequestHeader("Authorization") String token) {
        Long userId = jwtProvider.getUserIdFromToken(token, userRepository);
        User user = userRepository.findById(userId).orElseThrow();
        request.setUser(user);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus(ServiceRequestStatus.PENDING);
        return requestRepository.save(request);
    }

    @GetMapping
    public List<ServiceRequest> getAllRequests() {
        return requestRepository.findAll();
    }


    @GetMapping("/user/{userId}")
    public List<ServiceRequest> getUserRequestsById(@PathVariable Long userId) {
        return requestRepository.findByUserId(userId);
    }


    @GetMapping("/{id}")
    public ServiceRequest getRequestById(@PathVariable("id") Long id) {
        return requestRepository.findById(id).orElseThrow();
    }

    @GetMapping("/me")
    public List<ServiceRequest> getMyRequests(@RequestHeader("Authorization") String token) {
        Long userId = jwtProvider.getUserIdFromToken(token, userRepository);
        return requestRepository.findByUserId(userId);
    }


    @PutMapping("/{id}/status")
    public ServiceRequest updateStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") ServiceRequestStatus newStatus) {
        ServiceRequest request = requestRepository.findById(id).orElseThrow();
        ServiceRequestStatus currentStatus = request.getStatus();

        if (newStatus == ServiceRequestStatus.CANCELED && currentStatus != ServiceRequestStatus.PENDING) {
            throw new IllegalStateException("Замовлення можна скасувати лише, якщо воно в статусі 'Прийнято в обробку'");
        }

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Неможливий перехід статусу з " + currentStatus + " на " + newStatus);
        }

        request.setStatus(newStatus);
        return requestRepository.save(request);
    }


    @DeleteMapping("/{id}/cancel")
    public ServiceRequest cancelRequest(@PathVariable("id") Long id,
                                        @RequestHeader("Authorization") String token) {
        Long userId = jwtProvider.getUserIdFromToken(token, userRepository);
        ServiceRequest request = requestRepository.findById(id).orElseThrow();

        if (!request.getUser().getId().equals(userId)) {
            throw new SecurityException("Ви не маєте доступу до цієї заявки");
        }

        if (request.getStatus() != ServiceRequestStatus.PENDING) {
            throw new IllegalStateException("Заявку можна скасувати лише у статусі 'Прийнято в обробку'");
        }

        request.setStatus(ServiceRequestStatus.CANCELED);
        return requestRepository.save(request);
    }

    private boolean isValidStatusTransition(ServiceRequestStatus current, ServiceRequestStatus next) {
        return switch (current) {
            case PENDING -> next == ServiceRequestStatus.ACCEPTED || next == ServiceRequestStatus.CANCELED;
            case ACCEPTED -> next == ServiceRequestStatus.REPAIRED;
            case REPAIRED -> next == ServiceRequestStatus.COMPLETED;
            case COMPLETED, CANCELED -> false;
        };


    }

}
