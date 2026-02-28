package com.backend.lab.api.member.buyer.auth.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.buyer.auth.facade.BuyerAuthFacade;
import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.common.auth.annotation.RequireMemberRole;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerSocialRegisterReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/buyer/auth")
@RequiredArgsConstructor
@Tag(name = "[회원/매수자] 회원가입/탈퇴")
public class BuyerAuthController {

  private final BuyerAuthFacade buyerAuthFacade;

  @Operation(summary = "이메일 회원가입")
  @PostMapping("/register/local")
  public MemberWithTokenResp registerLocal(
      @RequestBody BuyerLocalRegisterReq req
  ) {
    return buyerAuthFacade.registerLocal(req);
  }

  @RequireMemberRole
  @Operation(summary = "소셜 회원가입 이후 정보 등록")
  @PostMapping("/register/social")
  public MemberWithTokenResp registerSocial(
      @RequestBody BuyerSocialRegisterReq req
  ) {
    return buyerAuthFacade.registerSocial(getUserId(), req);
  }
}
