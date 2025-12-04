package com.sesac.fmmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.fmmall.DTO.User.LoginRequestDto;
import com.sesac.fmmall.DTO.User.UserSaveRequestDto;
import com.sesac.fmmall.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AuthControllerTest {

    @Autowired
    WebTestClient webTestClient;

    final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    UserRepository userRepository;

    String testLoginId = "testUser01";
    String testPassword = "1234";

    @BeforeEach
    void setUp() throws Exception {

        UserSaveRequestDto signupDto = new UserSaveRequestDto();
        signupDto.setLoginId(testLoginId);
        signupDto.setPassword(testPassword);
        signupDto.setUserName("테스터");
        signupDto.setUserPhone("010-1111-2222");

        webTestClient.post().uri("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(signupDto))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() throws Exception {

        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setLoginId(testLoginId);
        loginDto.setPassword(testPassword);

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(loginDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").exists()
                .jsonPath("$.tokenType").isEqualTo("Bearer")
                .jsonPath("$.loginId").isEqualTo(testLoginId)
                .jsonPath("$.role").isEqualTo("USER");
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 틀림")
    void loginFailTest() throws Exception {

        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setLoginId(testLoginId);
        loginDto.setPassword("wrongPW");

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(loginDto))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
