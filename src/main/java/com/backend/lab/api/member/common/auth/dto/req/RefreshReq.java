package com.backend.lab.api.member.common.auth.dto.req;

import lombok.Getter;

@Getter
public class RefreshReq {

  private String refreshToken;
}
