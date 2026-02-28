package com.backend.lab.domain.member.core.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class PropertyAgentSearchResp extends BaseResp {
  private String name;
  private String businessName;
  private String phoneNumber;
  private String address;
}
