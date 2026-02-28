package com.backend.lab.api.admin.member.agent.facade.dto.req;

import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAgentSearchOptions extends PageOptions {

  private String query;
  private AgentTicketStatus status;
}
