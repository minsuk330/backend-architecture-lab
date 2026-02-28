package com.backend.lab.api.member.common.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("member-auth-redirectUrl")
@Tag(name = "[회원/공용] 로그인/인증")
public class MemberOauthExample {

  @Operation(summary = "콜백 redirect 예시")
  @GetMapping
  public void exampleCallback(
      @Parameter(description = "성공여부", required = true) @RequestParam("success") Boolean success,
      @Parameter(description = "결과 메시지", required = true) @RequestParam("message") String message,
      @Parameter(description = "최초로그인 여부(정보 입력창으로 넘어갈지)", required = true) @RequestParam("isFirstLogin") Boolean isFirstLogin,
      @Parameter(description = "accessToken", required = true) @RequestParam("accessToken") Boolean accessToken,
      @Parameter(description = "refreshToken", required = true) @RequestParam("refreshToken") Boolean refreshToken

  ) {
    throw new UnsupportedOperationException();
  }
}
