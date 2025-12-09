package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<Review> findAllByUser_UserId(int userId, Pageable pageable);
    Page<Review> findAllByOrderItem_OrderItemId(int orderItemId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE review AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
