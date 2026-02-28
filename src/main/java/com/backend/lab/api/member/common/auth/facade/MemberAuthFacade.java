package com.backend.lab.api.member.common.auth.facade;

import com.backend.lab.api.admin.auth.controller.ResetResp;
import com.backend.lab.api.member.common.auth.dto.req.LocalLoginReq;
import com.backend.lab.api.member.common.auth.dto.req.RefreshReq;
import com.backend.lab.api.member.common.auth.dto.req.SocialLoginUrlReq;
import com.backend.lab.api.member.common.auth.dto.resp.MemberEmailCheckResp;
import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.api.member.common.auth.dto.resp.SocialLoginUrlResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.backend.lab.common.mail.MailManager;
import com.backend.lab.common.oauth.service.OauthServiceFactory;
import com.backend.lab.common.util.StringUtil;
import com.backend.lab.domain.member.audit.accessLog.service.MemberAccessLogService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.service.MemberService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAuthFacade {

  private final Jwt jwt;
  private final MemberService memberService;
  private final MemberGlobalFacade memberCommonFacade;
  private final PasswordEncoder passwordEncoder;
  private final MemberAccessLogService memberAccessLogService;
  private final MailManager mailManager;

  private final OauthServiceFactory oauthServiceFactory;

  @Transactional
  public MemberWithTokenResp login(LocalLoginReq req, String clientIp) {

    try {
      Member member = memberService.getByEmail(req.getEmail());

      if (member.isBlocked()) {
        throw new BusinessException(ErrorCode.BLOCKED);
      }

      boolean matches = passwordEncoder.matches(req.getPassword(), member.getPassword());
      if (!matches) {
        throw new BusinessException(ErrorCode.LOGIN_FAILED);
      }

      TokenResponse tokenResponse = jwt.generateAllToken(
          Claims.from(member.getId(), member.getType().getRoleNames(), UserType.MEMBER));

      memberAccessLogService.createLoginLog(member.getId(), clientIp);

      return this.memberWithTokenResp(member, tokenResponse);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }
  }

  public SocialLoginUrlResp socialLogin(SocialLoginUrlReq req) {
    URI loginURI = oauthServiceFactory.getLoginURI(req.getProvider(), req.getRedirectUri());
    return SocialLoginUrlResp.builder()
        .provider(req.getProvider())
        .loginUrl(loginURI.toString())
        .build();
  }


  public MemberWithTokenResp refresh(RefreshReq req) {
    try {

      Claims claims = jwt.verify(req.getRefreshToken());
      Long memberId = claims.getMemberId();
      Member member = memberService.getById(memberId);

      TokenResponse tokenResponse = jwt.generateAllToken(
          Claims.from(member.getId(), member.getType().getRoleNames(), UserType.MEMBER));

      return this.memberWithTokenResp(member, tokenResponse);
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.ACCESS_DENIED);
    }
  }

  @Transactional
  public ResetResp sendResetPasswordMail(String email) {
    Member member = memberService.getByEmail(email);
    ProviderType provider = member.getProvider();
    if (provider == ProviderType.LOCAL) {
      String newPassword = StringUtil.generateRandomString(8);
      String newHashPassword = passwordEncoder.encode(newPassword);
      mailManager.sendResetPasswordMail(email, newPassword);
      memberService.updatePassword(member, newHashPassword);
      return ResetResp.builder()
          .isSocial(false)
          .providerType(provider)
          .build();

    }
    else {
      return ResetResp.builder()
          .isSocial(true)
          .providerType(provider)
          .build();
    }

  }

  @Transactional
  public void quit(Long userId) {
    memberService.delete(userId);
  }

  public MemberWithTokenResp memberWithTokenResp(Member member, TokenResponse tokenResponse) {
    return MemberWithTokenResp.builder()
        .member(memberCommonFacade.memberGlobalResp(member))
        .token(tokenResponse)
        .build();
  }

  @Transactional
  public void logout(String clientIp, Long userId) {
    memberAccessLogService.createLogoutLog(userId, clientIp);
  }

  public MemberEmailCheckResp checkEmail(String email) {
    boolean existsByEmail = memberService.existsByEmail(email);
    return MemberEmailCheckResp.builder()
        .isDuplicated(existsByEmail)
        .build();
  }
}
