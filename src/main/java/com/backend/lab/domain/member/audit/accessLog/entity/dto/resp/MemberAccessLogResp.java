package com.backend.lab.domain.member.audit.accessLog.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.member.audit.accessLog.entity.vo.MemberAccessLogType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccessLogResp extends BaseResp {

  private Long memberId;
  private String ipAddress;
  private MemberAccessLogType type;
}
