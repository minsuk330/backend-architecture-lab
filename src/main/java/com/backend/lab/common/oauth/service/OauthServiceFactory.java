package com.backend.lab.common.oauth.service;

import com.backend.lab.common.oauth.dto.OauthUserInfo;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthServiceFactory {

  private final AppleOauthService appleOauthService;
  private final KakaoOauthService kakaoOauthService;
  private final NaverOauthService naverOauthService;

  public URI getLoginURI(ProviderType provider, String redirectUri) {
    switch (provider) {
      case NAVER:
        return naverOauthService.getLoginURI(redirectUri);
      case KAKAO:
        return kakaoOauthService.getLoginURI(redirectUri);
      case APPLE:
        return appleOauthService.getLoginURI(redirectUri);
      default:
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }
  }

  public OauthUserInfo getProviderId(ProviderType provider, String payload, String state) {
    switch (provider) {
      case APPLE:
        return appleOauthService.getProviderId(payload, state);
      case KAKAO:
        return kakaoOauthService.getProviderId(payload, state);
      case NAVER:
        return naverOauthService.getProviderId(payload, state);
      default:
        throw new IllegalArgumentException("Unsupported provider for validation: " + payload);
    }
  }
}
