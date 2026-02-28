package com.backend.lab.api.member.common.auth.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.auth.controller.ResetResp;
import com.backend.lab.api.member.common.auth.dto.req.LocalLoginReq;
import com.backend.lab.api.member.common.auth.dto.req.RefreshReq;
import com.backend.lab.api.member.common.auth.dto.req.SocialLoginUrlReq;
import com.backend.lab.api.member.common.auth.dto.resp.MemberEmailCheckResp;
import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.api.member.common.auth.dto.resp.SocialLoginUrlResp;
import com.backend.lab.api.member.common.auth.facade.MemberAuthFacade;
import com.backend.lab.common.auth.annotation.RequireMemberRole;
import com.backend.lab.common.util.NetworkUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/auth")
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 로그인/인증")
public class MemberAuthController {

  private final MemberAuthFacade memberAuthFacade;

  @Operation(summary = "이메일 중복 검사")
  @GetMapping("/validate/email")
  public MemberEmailCheckResp checkEmail(
      @RequestParam("email") String email
  ) {
    return memberAuthFacade.checkEmail(email);
  }

  @Operation(summary = "이메일 로그인")
  @PostMapping("/login/local")
  public MemberWithTokenResp login(
      @RequestBody LocalLoginReq req,
      HttpServletRequest request
  ) {
    return memberAuthFacade.login(req, NetworkUtil.getClientIp(request));
  }

  @Operation(summary = "소셜 로그인")
  @PostMapping("/login/social")
  public SocialLoginUrlResp socialLogin(
      @RequestBody SocialLoginUrlReq req
  ) {
    return memberAuthFacade.socialLogin(req);
  }

  @Operation(summary = "토큰 갱신")
  @PostMapping("/refresh")
  public MemberWithTokenResp refresh(
      @RequestBody RefreshReq req
  ) {
    return memberAuthFacade.refresh(req);
  }

  @Operation(summary = "로그아웃")
  @RequireMemberRole
  @GetMapping("/logout")
  public void logout(
      HttpServletRequest request
  ) {
    memberAuthFacade.logout(NetworkUtil.getClientIp(request), getUserId());
  }

  @Operation(summary = "임시 비밀번호 메일 전송")
  @GetMapping("/send-reset-password-mail")
  public ResetResp sendResetPasswordMail(
      @RequestParam String email
  ) {
    return memberAuthFacade.sendResetPasswordMail(email);
  }

  @RequireMemberRole
  @DeleteMapping("/quit")
  public void quit() {
    memberAuthFacade.quit(getUserId());
  }
}
