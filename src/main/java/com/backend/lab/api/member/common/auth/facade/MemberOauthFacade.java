package com.backend.lab.api.member.common.auth.facade;

import com.backend.lab.api.member.common.auth.dto.resp.SocialLoginResp;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.common.oauth.dto.OauthUserInfo;
import com.backend.lab.common.oauth.service.OauthServiceFactory;
import com.backend.lab.domain.member.audit.accessLog.service.MemberAccessLogService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.service.MemberService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberOauthFacade {

  private final Jwt jwt;
  private final MemberService memberService;
  private final OauthServiceFactory oauthServiceFactory;
  private final MemberAccessLogService memberAccessLogService;

  @Transactional
  public SocialLoginResp handleNaver(String state, String code, String error,
      String errorDescription, String clientIp) {
    try {
      if (error != null) {
        return SocialLoginResp.builder()
            .success(false)
            .message(errorDescription)
            .build();
      } else {
        OauthUserInfo oauthUserInfo = oauthServiceFactory.getProviderId(ProviderType.NAVER, code,
            state);
        String providerId = oauthUserInfo.getProviderId();
        String email = oauthUserInfo.getEmail();
        Optional<Member> optionalMember = memberService.getOptionalByProviderId(ProviderType.NAVER,
            providerId);

        Member member = optionalMember.orElseGet(
            () -> memberService.createBySocialLogin(ProviderType.NAVER, providerId, email));

        TokenResponse token = jwt.generateAllToken(Claims.from(
            member.getId(),
            new String[]{"ROLE_MEMBER"},
            UserType.MEMBER
        ));

        memberAccessLogService.createLoginLog(member.getId(), clientIp);

        return SocialLoginResp.builder()
            .success(true)
            .message("success")
            .isFirstLogin(member.isFirstLogin())
            .accessToken(token.getAccessToken())
            .refreshToken(token.getRefreshToken())
            .type(member.getType())
            .build();
      }
    } catch (Exception e) {
      return SocialLoginResp.builder()
          .success(false)
          .message(URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8))
          .build();
    }
  }

  public SocialLoginResp handleKakao(String state, String code, String error,
      String errorDescription, String clientIp) {
    try {
      if (error != null) {
        return SocialLoginResp.builder()
            .success(false)
            .message(errorDescription)
            .build();
      } else {
        OauthUserInfo oauthUserInfo = oauthServiceFactory.getProviderId(ProviderType.KAKAO, code,
            state);
        String providerId = oauthUserInfo.getProviderId();
        String email = oauthUserInfo.getEmail();
        Optional<Member> optionalMember = memberService.getOptionalByProviderId(ProviderType.KAKAO,
            providerId);

        Member member = optionalMember.orElseGet(
            () -> memberService.createBySocialLogin(ProviderType.KAKAO, providerId, email));


        TokenResponse token = jwt.generateAllToken(Claims.from(
            member.getId(),
            new String[]{"ROLE_MEMBER"},
            UserType.MEMBER
        ));

        memberAccessLogService.createLoginLog(member.getId(), clientIp);

        return SocialLoginResp.builder()
            .success(true)
            .message("success")
            .isFirstLogin(member.isFirstLogin())
            .accessToken(token.getAccessToken())
            .refreshToken(token.getRefreshToken())
            .type(member.getType())
            .build();
      }
    } catch (Exception e) {
      return SocialLoginResp.builder()
          .success(false)
          .message(URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8))
          .build();
    }
  }

  public SocialLoginResp handleApple(String code, String state, String clientIp) {
    try {
      OauthUserInfo oauthUserInfo = oauthServiceFactory.getProviderId(ProviderType.APPLE, code,
          state);
      String providerId = oauthUserInfo.getProviderId();
      String email = oauthUserInfo.getEmail();
      Optional<Member> optionalMember = memberService.getOptionalByProviderId(ProviderType.APPLE,
          providerId);

      Member member = optionalMember.orElseGet(
          () -> memberService.createBySocialLogin(ProviderType.APPLE, providerId, email));

      TokenResponse token = jwt.generateAllToken(Claims.from(
          member.getId(),
          new String[]{"ROLE_MEMBER"},
          UserType.MEMBER
      ));

      memberAccessLogService.createLoginLog(member.getId(), clientIp);

      return SocialLoginResp.builder()
          .success(true)
          .message("success")
          .isFirstLogin(member.isFirstLogin())
          .accessToken(token.getAccessToken())
          .refreshToken(token.getRefreshToken())
          .type(member.getType())
          .build();
    } catch (Exception e) {
      return SocialLoginResp.builder()
          .success(false)
          .message(URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8))
          .build();
    }
  }
}
