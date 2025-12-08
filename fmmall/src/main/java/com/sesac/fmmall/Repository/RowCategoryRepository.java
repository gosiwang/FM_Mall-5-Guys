package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RowCategoryRepository extends JpaRepository<Category,Integer> {
}
