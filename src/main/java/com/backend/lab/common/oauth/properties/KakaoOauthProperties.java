package com.backend.lab.common.oauth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOauthProperties {

  private String clientId;
  private String clientSecret;
  private String callbackUrl;
  private String authorizationUrl;
}
