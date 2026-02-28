package com.backend.lab.api.member.global.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.resp.MemberAccessLogResp;
import com.backend.lab.domain.member.core.entity.dto.resp.AgentPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.BuyerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.CustomerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.MemberResp;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGlobalResp extends BaseResp {

  @JsonUnwrapped
  private MemberResp member;
  private MemberAccessLogResp lastLog;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private CustomerPropsResp customer;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private AgentPropsResp agent;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BuyerPropsResp buyer;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private SellerPropsResp seller;
}
