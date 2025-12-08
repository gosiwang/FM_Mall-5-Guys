package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Product.ProductRequestDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> findProductByProductId(@PathVariable int productId){
        ProductResponseDTO resultProduct = productService.findProductByProductId(productId);
        return ResponseEntity.ok(resultProduct);
    }

    @PostMapping("/insert")
    public ResponseEntity<ProductResponseDTO> insertProduct(@RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO newProduct = productService.createProduct(productRequestDTO);
        // 신규 리소스 생성 시 201 Created 상태 코드 반환.
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/modify/{productId}")
    public ResponseEntity<ProductResponseDTO> modifyProduct(@PathVariable int productId, @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO updateProduct = productService.modifyProduct(productId, productRequestDTO);
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int productId){
        productService.deleteProduct(productId);
        // 삭제 성공 시 내용 없이 204 No Content 응답.
        return ResponseEntity.ok().build();
    }
}
