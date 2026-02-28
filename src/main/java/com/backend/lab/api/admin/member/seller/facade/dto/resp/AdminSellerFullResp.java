package com.backend.lab.api.admin.member.seller.facade.dto.resp;

import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.domain.member.core.entity.dto.resp.MemberResp;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.backend.lab.domain.memberNote.entity.dto.resp.MemberNoteResp;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSellerFullResp {

  private Long id;
  private LocalDateTime createdAt;

  @JsonUnwrapped
  private MemberResp memberResp;

  @JsonUnwrapped
  private SellerPropsResp sellerPropsResp;

  private List<PropertyRequestAgentResp> propertyRequests;
  private List<MemberNoteResp> notes;
}
