package com.backend.lab.api.admin.organization.user.dto.resp;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminActiveResp {

  @JsonUnwrapped
  private AdminGlobalResp adminGlobalResp;
  private Boolean isActive;
}
