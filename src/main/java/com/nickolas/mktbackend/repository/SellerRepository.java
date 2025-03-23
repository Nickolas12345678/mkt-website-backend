package com.nickolas.mktbackend.repository;

import com.nickolas.mktbackend.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller,Long> {
    Seller findByEmail(String email);
    Optional<Seller> findById(Long id);
}
