package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
