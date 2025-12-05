package com.sesac.fmmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.fmmall.DTO.User.LoginRequestDto;
import com.sesac.fmmall.DTO.User.UserSaveRequestDto;
import com.sesac.fmmall.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    String testLoginId = "testUser02";
    String testPassword = "1234";

    @Test
    @BeforeEach
    void setUp() throws Exception {

        // 🔥 먼저 전부 지우고 시작
        userRepository.deleteAll();

        UserSaveRequestDto signupDto = new UserSaveRequestDto();
        signupDto.setLoginId(testLoginId);
        signupDto.setPassword(testPassword);
        signupDto.setUserName("테스터");
        signupDto.setUserPhone("010-1111-2222");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccessTest() throws Exception {

        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setLoginId(testLoginId);
        loginDto.setPassword(testPassword);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.loginId").value(testLoginId))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 틀림")
    void loginFailTest() throws Exception {

        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setLoginId(testLoginId);
        loginDto.setPassword("wrongPW");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().is4xxClientError());
    }
}
