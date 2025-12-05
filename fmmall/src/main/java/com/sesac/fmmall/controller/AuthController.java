// com.sesac.fmmall.Controller.AuthController

package com.sesac.fmmall.controller;

import com.sesac.fmmall.DTO.User.*;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Service.UserService;
import com.sesac.fmmall.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSaveRequestDto dto) {
        UserResponseDto response = userService.signup(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        User user = userService.login(dto.getLoginId(), dto.getPassword());
        String token = jwtTokenProvider.createToken(user);
        TokenResponseDto response = new TokenResponseDto(
                token,
                "Bearer",
                user.getLoginId(),
                user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }
}
