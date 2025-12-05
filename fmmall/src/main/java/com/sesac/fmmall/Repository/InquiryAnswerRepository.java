package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.InquiryAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Integer> {
//    /* 전달 받은 정렬 기준으로 조회 (페이징) */
//    Page<Inquiry> findAll(Pageable pageable);

    Page<InquiryAnswer> findAllByOrderByUpdatedAtDesc(Pageable pageable);

}
