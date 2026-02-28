package com.backend.lab.api.member.agent.auth.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.auth.facade.AgentAuthFacade;
import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.common.auth.annotation.RequireMemberRole;
import com.backend.lab.domain.member.core.entity.dto.req.AgentLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.AgentSocialRegisterReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/auth")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 회원가입/탈퇴")
public class AgentAuthController {

  private final AgentAuthFacade agentAuthFacade;

  // 회원가입
  @Operation(summary = "이메일 회원가입")
  @PostMapping("/register/local")
  public MemberWithTokenResp register(
      @RequestBody AgentLocalRegisterReq req
  ) {
    return agentAuthFacade.registerLocal(req);
  }

  @RequireMemberRole
  @Operation(summary = "소셜 회원가입 이후 정보 등록")
  @PostMapping("/register/social")
  public MemberWithTokenResp registerSocial(
      @RequestBody AgentSocialRegisterReq req
  ) {
    return agentAuthFacade.registerSocial(getUserId(), req);
  }
}
