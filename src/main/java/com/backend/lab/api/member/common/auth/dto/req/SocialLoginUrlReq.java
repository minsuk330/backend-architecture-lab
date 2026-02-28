package com.backend.lab.api.member.common.auth.dto.req;

import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import lombok.Getter;

@Getter
public class SocialLoginUrlReq {
  private ProviderType provider;
  private String redirectUri;
}
