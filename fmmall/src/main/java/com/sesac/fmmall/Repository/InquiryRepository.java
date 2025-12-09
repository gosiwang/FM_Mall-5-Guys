package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
//    /* 전달 받은 정렬 기준으로 조회 (페이징) */
//    Page<Inquiry> findAll(Pageable pageable);

//    Page<Inquiry> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    Page<Inquiry> findAllByUser_UserId(int userId, Pageable pageable);
    Page<Inquiry> findAllByProduct_ProductId(int productId, Pageable pageable);

}
