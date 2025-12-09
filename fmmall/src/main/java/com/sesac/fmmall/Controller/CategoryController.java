package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /* 추가할 상위 카테고리를 입력 후 , 상위 카테고리를 추가/등록 */
    @PostMapping("/insert")
    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody @Valid CategoryDTO categoryDTO) {

        // 서비스 레이어에 DTO 전달해서 저장 로직 수행
        CategoryDTO savedCategory = categoryService.insertCategory(categoryDTO);

        // 생성된 카테고리 정보를 201 Created 로 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedCategory);
    }

    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 수정 진행. */
    @PutMapping("/modify/{categoryId}")
    public ResponseEntity<CategoryDTO> modifyCategory(@PathVariable int categoryId, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.modifyCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 삭제 진행 */
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    /* categoryId값을 넘겨 해당 상위카테고리의 전체 상품 목록 조회. */
    @GetMapping("/findAll/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getAllProductsByCategory(@PathVariable int categoryId) {
        List<ProductResponseDTO> products = categoryService.findAllProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

}
