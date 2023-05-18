package com.ll.rest_api.boundedContext.member.controller;

import com.ll.rest_api.base.rsData.RsData;
import com.ll.rest_api.boundedContext.member.entity.Member;
import com.ll.rest_api.boundedContext.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
// 알아서 @ResponseBody를 붙여 줌
@RequestMapping(value = "/api/v1/member", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
// produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE : 내가 생성하는 것도 JSON이고, 받는 것도 JSON이라고 알려주는 메타 정보
@Tag(name = "ApiV1MemberController", description = "로그인, 로그인 된 회원의 정보")
// API 작업을 그룹화하는 데 사용. API 문서화 도구에서는 이러한 태그를 사용하여 API 작업을 카테고리화하고 필터링할 수 있음
public class ApiV1MemberController {
    private final MemberService memberService;

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @AllArgsConstructor
    @Getter
    public static class LoginResponse {
        private final String accessToken;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인, 엑세스 토큰 발급")
    // API 작업(operation)에 대한 정보를 제공하는 데 사용하는 어노테이션. 이 어노테이션을 사용하여 API 작업의 제목, 설명, 응답 타입 등을 지정
    public RsData<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String accessToken = memberService.genAccessToken(loginRequest.getUsername(), loginRequest.getPassword());
        // 토큰 발급

        // resp.addHeader("Authentication", accessToken);
        // 응답 헤더는 HttpServletResponse의 addHeader를 통해서 원하는 내용으로 만들 수 있음 -> 아이디랑 비번으로 로그인 하면 토큰을 받을 수 있게 됨

        return RsData.of(
                "S-1",
                "엑세스토큰이 생성되었습니다.",
                new LoginResponse(accessToken)
        ); // 도입 후 훨씬 정돈됨
    }

    @AllArgsConstructor
    @Getter
    public static class MeResponse {
        private final Member member;
    }

    // consumes = ALL_VALUE => 나는 딱히 JSON 을 입력받기를 고집하지 않겠다.
    @GetMapping(value = "/me", consumes = ALL_VALUE)
    @Operation(summary = "로그인된 사용자의 정보", security = @SecurityRequirement(name = "bearerAuth"))
    public RsData<MeResponse> me(@AuthenticationPrincipal User user) {
        Member member = memberService.findByUsername(user.getUsername()).get();

        return RsData.of(
                "S-1",
                "성공",
                new MeResponse(member)
        );
    }
}