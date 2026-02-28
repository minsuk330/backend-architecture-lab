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
public class AdminAgentSearchResp {

  private Long id;
  private String email;
  private String name;
  private String businessName;
  private String phoneNumber;

  private String ticketName;
  private LocalDateTime ticketStartAt;
  private LocalDateTime ticketEndAt;

  private Integer showContactCount;
  private Integer advertiseCount;

  private LocalDateTime createdAt;
  private LocalDateTime deletedAt;

  private String address;
  private String addressDetail;

  private AgentTicketStatus status;
}
