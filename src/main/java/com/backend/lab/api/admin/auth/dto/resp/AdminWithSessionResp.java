package com.backend.lab.api.admin.auth.dto.resp;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.common.auth.session.SessionResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWithSessionResp {

  @JsonUnwrapped
  private AdminGlobalResp admin;
  private SessionResponse session;

  public AdminWithSessionResp(AdminGlobalResp admin, String sessionId) {
    this.admin = admin;
    this.session = new SessionResponse(sessionId, LocalDateTime.now().plusHours(1));
  }
}
