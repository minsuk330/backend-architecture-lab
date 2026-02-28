package com.backend.lab.api.admin.member.agent.facade.dto.resp;

import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.api.member.agent.payment.facade.item.dto.AgentMyItemResp;
import com.backend.lab.domain.member.core.entity.dto.resp.AgentPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.MemberResp;
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
public class AdminAgentFullResp {

  private Long id;
  private LocalDateTime createdAt;
  private AgentTicketStatus status;

  @JsonUnwrapped
  private MemberResp memberResp;

  @JsonUnwrapped
  private AgentPropsResp agentPropsResp;

  private ItemWithTotalPrice item;

  private List<PropertyRequestAgentResp> propertyRequests;
  private List<MemberNoteResp> notes;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ItemWithTotalPrice {
    @JsonUnwrapped
    private AgentMyItemResp item;
    private Integer totalPrice;
  }
}
