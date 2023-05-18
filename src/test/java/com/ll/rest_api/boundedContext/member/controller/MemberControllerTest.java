package com.ll.rest_api.boundedContext.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MemberControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("POST /member/login 은 로그인 처리 URL 이다.")
    void t1() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                                // REST를 할 때는 보통 폼 전송을 안 하고, JSON으로 POST 전송
                                // 안드로이드나 리액트 등이랑 통신할 때는 CSRF 토큰을 사용하기 어려움(폼 방식에서 쓰기 쉬움)
                                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("S-1"))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("GET /member/me 는 내 정보를 조회하는 URL 이다.")
    void t2() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/member/me")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.resultCode").value("S-1"))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data.member.id").exists())
                .andExpect(jsonPath("$.data.member.username").value("user1"));
        // 세션을 꺼둬서 User를 찾지 못하는(로그인이 안 되는) 상황
        // t2 테스트케이스도 원래는 로그인이 안 되어야 하는 상황
        // 하지만 @WithUserDetails("user1") 어노테이션이 상황을 해결
        // 해당 어노테이션을 사용하면 UserDetailsService 객체를 통해서 User 객체를 바로 받아서 사용
    }
}