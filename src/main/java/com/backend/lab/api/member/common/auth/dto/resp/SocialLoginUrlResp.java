package com.backend.lab.api.member.common.auth.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginUrlResp {

  private ProviderType provider;
  private String loginUrl;
}
