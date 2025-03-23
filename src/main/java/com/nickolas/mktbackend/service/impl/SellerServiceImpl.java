package com.nickolas.mktbackend.service.impl;

import com.nickolas.mktbackend.config.JwtProvider;
import com.nickolas.mktbackend.domain.Role;
import com.nickolas.mktbackend.exception.SellerException;
import com.nickolas.mktbackend.model.Seller;
import com.nickolas.mktbackend.model.User;
import com.nickolas.mktbackend.repository.SellerRepository;
import com.nickolas.mktbackend.repository.UserRepository;
import com.nickolas.mktbackend.response.AuthResponse;
import com.nickolas.mktbackend.response.SellerResponse;
import com.nickolas.mktbackend.service.OTPService;
import com.nickolas.mktbackend.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final OTPService otpService;

    @Override
    public AuthResponse createSeller(Seller seller) throws SellerException {
        if (userRepository.findByEmail(seller.getEmail()).isPresent()) {
            throw new SellerException("Продавець з таким email вже існує.");
        }

        User newUser = new User();
        newUser.setEmail(seller.getEmail());
        newUser.setUsername(seller.getName());
        newUser.setPassword(passwordEncoder.encode(seller.getPassword()));
        newUser.setRole(Role.ROLE_SELLER);
        userRepository.save(newUser);

        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setName(seller.getName());
        newSeller.setRole(Role.ROLE_SELLER);
        newSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        sellerRepository.save(newSeller);

       // otpService.generateOtp(seller.getEmail());


        GrantedAuthority authority = new SimpleGrantedAuthority(newUser.getRole().name());


        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                newUser.getEmail(), newUser.getPassword(), List.of(authority));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtProvider.generateToken(authentication);

        return new AuthResponse(token, Role.ROLE_SELLER.name(), "Продавця створено. Підтвердіть email перед авторизацією.");
    }

    @Override
    public Seller getSellerById(Long id) throws SellerException {
        return sellerRepository.findById(id)
                .orElseThrow(() -> new SellerException("Продавця з ID " + id + " не знайдено."));
    }

    @Override
    public Seller getSellerByEmail(String email) throws SellerException {
        Seller seller = sellerRepository.findByEmail(email);
        if (seller == null) {
            throw new SellerException("Продавця з таким email не знайдено.");
        }
        return seller;
    }

    @Override
    public Seller getSellerProfile(String jwt) throws SellerException {
        String email = jwtProvider.getEmailFromToken(jwt);
        return getSellerByEmail(email);
    }

    @Override
    public List<Seller> getAllSellers() {
        return sellerRepository.findAll();
    }

    @Override
    public Seller updateSeller(Long id, Seller sellerDetails) throws SellerException {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerException("Продавця з ID " + id + " не знайдено."));

        seller.setName(sellerDetails.getName());
        seller.setEmail(sellerDetails.getEmail());
        seller.setPassword(passwordEncoder.encode(sellerDetails.getPassword()));

        return sellerRepository.save(seller);
    }

    @Override
    public void deleteSeller(Long id) throws SellerException {
        if (!sellerRepository.existsById(id)) {
            throw new SellerException("Продавця з ID " + id + " не знайдено.");
        }
        sellerRepository.deleteById(id);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws SellerException {
        if (!otpService.verifyOtp(email, otp)) {
            throw new SellerException("Неправильний або протермінований OTP.");
        }
        Seller seller = getSellerByEmail(email);
        seller.setEmailVerified(true);
        return sellerRepository.save(seller);
    }

}
