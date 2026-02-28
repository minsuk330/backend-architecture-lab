package com.backend.lab.domain.agentItem.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;
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
public class AgentItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agent_id", nullable = false)
  private Member agent;

  @OneToMany(mappedBy = "agentItem", cascade = CascadeType.REMOVE)
  private Set<AgentTicket> tickets;

  private Integer showContactCount;
  private Integer advertiseCount;
  private Boolean hasPurchasedAdvertise;

  private Integer dailyFreeShowContactCount;
  private LocalDateTime lastUseDaily;
}
