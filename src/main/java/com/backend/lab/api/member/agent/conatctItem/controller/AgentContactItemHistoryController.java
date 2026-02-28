package com.backend.lab.api.member.agent.conatctItem.controller;

import com.backend.lab.api.member.agent.conatctItem.facade.AgentContactItemFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/contact")
@Tag(name = "[회원/공인중개사] 매도자 연락처 열람 관련")
public class AgentContactItemHistoryController {

  private final AgentContactItemFacade agentContactItemFacade;

  @Operation(summary = "사용 기록")
  @GetMapping
  public PageResp<ContactItemHistoryResp> gets(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return agentContactItemFacade.gets(options, AuthUtil.getUserId());
  }

}
