package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<Review> findAllByUserUserId(int userId, Pageable pageable);
    Page<Review> findAllByOrderItemOrderItemId(int orderItemId, Pageable pageable);
}
