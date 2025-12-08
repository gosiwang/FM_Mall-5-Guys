package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.User.*;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Service.UserService;
import com.sesac.fmmall.Security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/User")
@RequiredArgsConstructor
public class UserController {

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

    @GetMapping("/findOne/{userId}")
    public ResponseEntity<UserResponseDto> findOne(@PathVariable Integer userId) {
        UserResponseDto response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/modify/{userId}")
    public ResponseEntity<UserResponseDto> modify(
            @PathVariable Integer userId,
            @RequestBody UserUpdateRequestDto dto) {

        UserResponseDto response = userService.updateUser(userId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> request) {

        String password = request.get("password");
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        userService.deleteUser(userId, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}