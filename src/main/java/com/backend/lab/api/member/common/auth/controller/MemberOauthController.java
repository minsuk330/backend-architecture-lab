package com.backend.lab.api.member.common.auth.controller;

import com.backend.lab.api.member.common.auth.dto.resp.SocialLoginResp;
import com.backend.lab.api.member.common.auth.facade.MemberOauthFacade;
import com.backend.lab.common.util.NetworkUtil;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Hidden
@Slf4j
public class MemberOauthController {

  private final MemberOauthFacade memberOauthFacade;

  @GetMapping("/naver/callback")
  public RedirectView naverCallback(
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription,
      HttpServletRequest request
  ) {
    SocialLoginResp resp = memberOauthFacade.handleNaver(
        state, code, error, errorDescription, NetworkUtil.getClientIp(request)
    );
    return this.redirectView(resp, state);
  }

  @GetMapping("/kakao/callback")
  public RedirectView kakaoCallback(
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "error", required = false) String error,
      @RequestParam(value = "error_description", required = false) String errorDescription,
      HttpServletRequest request
  ) {
    SocialLoginResp resp = memberOauthFacade.handleKakao(state, code, error, errorDescription, NetworkUtil.getClientIp(request));
    return this.redirectView(resp, state);
  }

  @PostMapping("/apple/callback")
  public RedirectView appleCallback(
      @RequestParam("code") String code,
      @RequestParam("state") String state,
      HttpServletRequest request
  ) {
    log.info("\ncode={}\nstate={}\n", code, state);
    SocialLoginResp resp = memberOauthFacade.handleApple(code, state, NetworkUtil.getClientIp(request));
    return this.redirectView(resp, state);
  }

  public RedirectView redirectView(SocialLoginResp resp, String nextUrl) {
    String next = nextUrl;
    if (!next.contains("/")) {
      next = URLDecoder.decode(nextUrl, StandardCharsets.UTF_8);
    }
    RedirectView redirectView = new RedirectView();
    String nextUri = UriComponentsBuilder.fromUri(URI.create(next))
        .queryParam("success", resp.isSuccess())
        .queryParam("isFirstLogin", resp.getIsFirstLogin())
        .queryParam("message", resp.getMessage())
        .queryParam("accessToken", resp.getAccessToken())
        .queryParam("refreshToken", resp.getRefreshToken())
        .queryParam("type", resp.getType() != null ? resp.getType() : "")
        .build()
        .toUriString();
    redirectView.setUrl(nextUri);
    return redirectView;
  }
}
