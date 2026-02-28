package com.backend.lab.api.member.agent.payment.facade.item.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentMyItemResp {

  private Boolean hasTicket;
  private String ticketName;
  private LocalDateTime ticketStartAt;
  private LocalDateTime ticketEndAt;

  private Integer showContactCount;
  private Integer advertiseCount;

  private Integer dailyFreeShowContactCount;
}
