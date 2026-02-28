package com.backend.lab.api.member.common.auth.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResp {

  private boolean success;
  private Boolean isFirstLogin;
  private String message;
  private String accessToken;
  private String refreshToken;
  private MemberType type;
}
