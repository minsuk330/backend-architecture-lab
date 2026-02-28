package com.backend.lab.api.member.agent.me.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.me.facade.AgentMeFacade;
import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.domain.member.core.entity.dto.req.AgentUpdateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequestMapping("/agent/me")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 내 정보 관리")
public class AgentMeController {

  private final AgentMeFacade agentMeFacade;

  @Operation(summary = "내 정보 조회")
  @GetMapping
  public MemberGlobalResp me() {
    return agentMeFacade.me(getUserId());
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping
  public void update(
      @RequestBody AgentUpdateReq req
  ) {
    agentMeFacade.update(getUserId(), req);
  }
}
