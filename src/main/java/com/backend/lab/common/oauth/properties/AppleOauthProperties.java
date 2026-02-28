package com.backend.lab.common.oauth.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOauthProperties {

  private String clientId;
  private String keyId;
  private String teamId;
  private String keyPath;
  private String callbackUrl;
  private String authorizationUrl;
}
