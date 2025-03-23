package com.nickolas.mktbackend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "wishlist_items")
@Data
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishlist;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
