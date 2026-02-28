package com.backend.lab.domain.admin.audit.accessLog.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AdminAccessLogType;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AgentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "admin_access_log")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdminAccessLog extends BaseEntity {

  private Long adminId;
  private String ipAddress;
  private AgentType agentType;
  private AdminAccessLogType type;
}
