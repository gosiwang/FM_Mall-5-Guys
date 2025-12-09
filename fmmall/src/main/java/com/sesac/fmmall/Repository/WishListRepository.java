package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.WishList;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Integer> {

    @Query("SELECT w.wishListId FROM WishList w WHERE w.user.userId = :userId AND w.product.productId = :productId")
    Optional<Integer> findIdByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);
//    Optional<Integer> findByUser_UserIdAndProduct_ProductId(int userId, int productId);
    Page<WishList> findAllByUser_UserId(int userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE wish_list AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
