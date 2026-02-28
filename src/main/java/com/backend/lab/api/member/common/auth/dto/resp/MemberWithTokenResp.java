package com.backend.lab.api.member.common.auth.dto.resp;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithTokenResp {

  @JsonUnwrapped
  private MemberGlobalResp member;
  private TokenResponse token;
}
