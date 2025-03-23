package com.nickolas.mktbackend.service;

import com.nickolas.mktbackend.domain.Role;
import com.nickolas.mktbackend.exception.SellerException;
import com.nickolas.mktbackend.model.Seller;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.SellerRepository;
import com.nickolas.mktbackend.repository.UserRepository;
import com.nickolas.mktbackend.response.AuthResponse;
import com.nickolas.mktbackend.response.SellerResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SellerService {
    Seller getSellerProfile(String jwt) throws SellerException;
    AuthResponse createSeller(Seller seller) throws SellerException;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email) throws SellerException;
    List<Seller> getAllSellers();
    Seller updateSeller(Long id, Seller seller) throws SellerException;
    void deleteSeller(Long id) throws SellerException;
    Seller verifyEmail(String email, String otp) throws SellerException;
}

