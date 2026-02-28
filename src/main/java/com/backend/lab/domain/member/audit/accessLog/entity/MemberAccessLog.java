package com.backend.lab.domain.member.audit.accessLog.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.audit.accessLog.entity.vo.MemberAccessLogType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_access_log")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAccessLog extends BaseEntity {

  private Long memberId;
  private String ipAddress;
  private MemberAccessLogType type;
}
