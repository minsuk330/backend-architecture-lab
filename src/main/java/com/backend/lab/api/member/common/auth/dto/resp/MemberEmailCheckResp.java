package com.backend.lab.api.member.common.auth.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmailCheckResp {

  private boolean isDuplicated;
}
