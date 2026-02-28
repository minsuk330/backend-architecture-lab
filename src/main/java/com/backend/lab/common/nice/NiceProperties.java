package com.backend.lab.common.nice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "nice")
public class NiceProperties {

  private String clientId;
  private String clientSecret;
  private String productId;
  private String accessToken;
  private String callbackUrl;
}
