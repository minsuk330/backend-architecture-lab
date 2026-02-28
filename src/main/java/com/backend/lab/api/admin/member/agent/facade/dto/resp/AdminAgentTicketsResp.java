package com.backend.lab.api.admin.member.agent.facade.dto.resp;

import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAgentTicketsResp {

  private String name;
  private LocalDateTime ticketStartAt;
  private LocalDateTime ticketEndAt;
  private AgentTicketStatus status;
}
