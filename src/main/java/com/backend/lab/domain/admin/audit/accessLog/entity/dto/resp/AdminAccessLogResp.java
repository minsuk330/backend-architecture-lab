package com.backend.lab.domain.admin.audit.accessLog.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AdminAccessLogType;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AgentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAccessLogResp extends BaseResp {

  private Long memberId;
  private String ipAddress;
  private AgentType agentType;
  private AdminAccessLogType type;
}
