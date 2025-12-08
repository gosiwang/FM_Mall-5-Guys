package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Payment.*;
import com.sesac.fmmall.Service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentMethodService paymentMethodService;

    @PostMapping("/insert/{userId}")
    public ResponseEntity<PaymentMethodResponseDto> insert(
            @PathVariable Integer userId,
            @Valid @RequestBody PaymentMethodSaveRequestDto dto) {
        PaymentMethodResponseDto response = paymentMethodService.addPaymentMethod(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDto>> findAll(@PathVariable Integer userId) {
        List<PaymentMethodResponseDto> responses = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/findOne/{paymentMethodId}/{userId}")
    public ResponseEntity<PaymentMethodResponseDto> findOne(
            @PathVariable Integer paymentMethodId,
            @PathVariable Integer userId) {
        PaymentMethodResponseDto response = paymentMethodService.getPaymentMethodById(paymentMethodId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/modify/{paymentMethodId}/{userId}")
    public ResponseEntity<PaymentMethodResponseDto> modify(
            @PathVariable Integer paymentMethodId,
            @PathVariable Integer userId,
            @Valid @RequestBody PaymentMethodSaveRequestDto dto) {
        PaymentMethodResponseDto response = paymentMethodService.updatePaymentMethod(paymentMethodId, userId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{paymentMethodId}/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer paymentMethodId,
            @PathVariable Integer userId) {
        paymentMethodService.deletePaymentMethod(paymentMethodId, userId);
        return ResponseEntity.noContent().build();
    }
}