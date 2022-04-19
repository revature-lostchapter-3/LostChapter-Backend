package com.revature.lostchapterbackend.dao;

import com.revature.lostchapterbackend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistDAO extends JpaRepository<Wishlist, Integer> {
}
