package com.sesac.fmmall.controller;

import com.sesac.fmmall.DTO.Address.*;
import com.sesac.fmmall.Service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/insert/{userId}")
    public ResponseEntity<AddressResponseDto> insert(
            @PathVariable Integer userId,
            @Valid @RequestBody AddressSaveRequestDto dto) {
        AddressResponseDto response = addressService.addAddress(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<AddressResponseDto>> findAll(@PathVariable Integer userId) {
        List<AddressResponseDto> responses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/findOne/{addressId}/{userId}")
    public ResponseEntity<AddressResponseDto> findOne(
            @PathVariable Integer addressId,
            @PathVariable Integer userId) {
        AddressResponseDto response = addressService.getAddressById(addressId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/modify/{addressId}/{userId}")
    public ResponseEntity<AddressResponseDto> modify(
            @PathVariable Integer addressId,
            @PathVariable Integer userId,
            @Valid @RequestBody AddressSaveRequestDto dto) {
        AddressResponseDto response = addressService.updateAddress(addressId, userId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{addressId}/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer addressId,
            @PathVariable Integer userId) {
        addressService.deleteAddress(addressId, userId);
        return ResponseEntity.noContent().build();
    }
}