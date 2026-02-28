package com.backend.lab.domain.agentItem.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class AgentTicket extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agent_item_id")
  private AgentItem agentItem;

  public void setAgentItem(AgentItem agentItem) {
    this.agentItem = agentItem;
    if (agentItem != null) {
      agentItem.getTickets().add(this);
    }
  }

  private String name;
  private LocalDateTime ticketStartAt;
  private LocalDateTime ticketEndAt;
}
