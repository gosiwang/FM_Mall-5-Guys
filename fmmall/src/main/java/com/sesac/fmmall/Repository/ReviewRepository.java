package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<Review> findAllByUser_UserId(int userId, Pageable pageable);
    Page<Review> findAllByOrderItem_OrderItemId(int orderItemId, Pageable pageable);
}
