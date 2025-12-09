package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.DTO.RowCategoryDTO;
import com.sesac.fmmall.Service.RowCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/RowCategory")
@RequiredArgsConstructor
public class RowCategoryController {
    private final RowCategoryService rowCategoryService;

    // 하위 카테고리 생성
    @PostMapping("/insert")
    public ResponseEntity<RowCategoryDTO> insertRowCategory(@RequestBody RowCategoryDTO rowCategoryDTO) {

        RowCategoryDTO savedRowCategory = rowCategoryService.insertRowCategory(rowCategoryDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRowCategory);
    }

    // 하위 카테고리 네임 수정
    @PutMapping("/modify/{rowCategoryId}")
    public ResponseEntity<RowCategoryDTO> modifyRowCategory(@PathVariable int rowCategoryId,
                                                            @RequestBody RowCategoryDTO rowCategoryDTO) {
        RowCategoryDTO updatedRowCategory = rowCategoryService.modifyRowCategory(rowCategoryId, rowCategoryDTO);

        return ResponseEntity.ok(updatedRowCategory);
    }

    // 하위 카테고리 삭제
    @DeleteMapping("/delete/{rowCategoryId}")
    public ResponseEntity<Void> deleteRowCategory(@PathVariable int rowCategoryId) {
        rowCategoryService.deleteRowCategory(rowCategoryId);
        return ResponseEntity.noContent().build();
    }

    // 하위 카테고리 상품 전체 조회.
    @GetMapping("/findAll/{rowCategoryId}")
    public ResponseEntity<List<ProductResponseDTO>> findAllProductsByRowCategory(@PathVariable int rowCategoryId) {
        List<ProductResponseDTO> products = rowCategoryService.findAllProductsByRowCategoryId(rowCategoryId);
        return ResponseEntity.ok(products);
    }
}
