package com.backend.lab.api.admin.auth.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.auth.dto.req.LocalLoginReq;
import com.backend.lab.api.admin.auth.dto.req.ResetPasswordReq;
import com.backend.lab.api.admin.auth.dto.resp.AdminAuthenticationToken;
import com.backend.lab.api.admin.auth.dto.resp.AdminWithSessionResp;
import com.backend.lab.api.admin.auth.facade.AdminAuthFacade;
import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.auth.session.AdminSessionManager;
import com.backend.lab.common.util.NetworkUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Tag(name = "[관리자] 로그인/인증")
public class AdminAuthController {

  private final AdminAuthFacade adminAuthFacade;
  private final AdminSessionManager adminSessionManager;

  @Operation(summary = "이메일 로그인")
  @PostMapping("/login/local")
  public AdminWithSessionResp login(
      @RequestBody LocalLoginReq req,
      HttpServletRequest httpServletRequest
  ) {
    AdminAuthenticationToken authenticationToken = adminAuthFacade.login(
        req,
        NetworkUtil.getClientIp(httpServletRequest),
        NetworkUtil.getAgentType(httpServletRequest)
    );
    String sessionId = adminSessionManager.register(httpServletRequest, authenticationToken);
    AdminGlobalResp adminResp = authenticationToken.getAdmin();
    return new AdminWithSessionResp(adminResp, sessionId);
  }

  @Operation(summary = "세션 갱신")
  @RequireAdminRole
  @PostMapping("/refresh")
  public AdminWithSessionResp refresh(
      HttpServletRequest httpServletRequest
  ) {
    String sessionId = adminSessionManager.refresh(getUserId(), httpServletRequest);
    AdminGlobalResp adminResp = adminAuthFacade.adminResp(getUserId());
    return new AdminWithSessionResp(adminResp, sessionId);
  }

  @Operation(summary = "임시 비밀번호 발급 이메일 전송")
  @PostMapping("/reset-password")
  public void resetPassword(
      @RequestBody ResetPasswordReq req
  ) {
    adminAuthFacade.sendResetPasswordEmail(req);
  }

  @Operation(summary = "로그아웃")
  @RequireAdminRole
  @PostMapping("/logout")
  public void logout(HttpServletRequest httpServletRequest) {
    adminAuthFacade.logout(
        getUserId(),
        NetworkUtil.getClientIp(httpServletRequest),
        NetworkUtil.getAgentType(httpServletRequest)
    );
    adminSessionManager.invalidate(httpServletRequest);
  }
}
