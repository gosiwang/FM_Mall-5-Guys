package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.BrandDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    /* 브랜드 정보 등록 */
    @PostMapping("/insert")
    public ResponseEntity<BrandDTO> insertBrand(@RequestBody @Valid BrandDTO brandDTO) {

        BrandDTO savedBrand = brandService.insertBrand(brandDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedBrand);
    }

    /* 브랜드 정보 수정 */
    @PutMapping("/modify/{brandId}")
    public ResponseEntity<BrandDTO> modifyBrand(@PathVariable int brandId,
                                                @RequestBody @Valid BrandDTO brandDTO) {

        BrandDTO updatedBrand = brandService.modifyBrand(brandId, brandDTO);

        return ResponseEntity.ok(updatedBrand);
    }

    /* 브랜드 정보 삭제 */
    @DeleteMapping("/delete/{brandId}")
    public ResponseEntity<Void> deleteBrand(@PathVariable int brandId) {

        brandService.deleteBrand(brandId);

        return ResponseEntity.noContent().build();
    }

    /* 브랜드 상품 목록 조회 */
    @GetMapping("/findAll/{brandId}")
    public ResponseEntity<List<ProductResponseDTO>> findAllProductsByBrand(@PathVariable int brandId) {

        List<ProductResponseDTO> products = brandService.findAllProductsByBrandId(brandId);

        return ResponseEntity.ok(products);
    }
}
