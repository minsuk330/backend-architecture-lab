package com.backend.lab.api.admin.accessLog.dto.resp;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.dto.resp.AdminAccessLogResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAccessLogApiResp {

  private AdminAccessLogResp log;
  private AdminGlobalResp admin;
}
