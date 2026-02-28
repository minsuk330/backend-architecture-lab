package com.backend.lab.api.admin.accessLog.dto.resp;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.resp.MemberAccessLogResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccessLogApiResp {

  private MemberAccessLogResp log;
  private MemberGlobalResp member;
}
