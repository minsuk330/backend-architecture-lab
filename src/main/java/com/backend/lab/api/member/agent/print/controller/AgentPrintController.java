package com.backend.lab.api.member.agent.print.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.print.facade.AgentPrintFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.domain.print.entity.dto.req.PrintUpdateReq;
import com.backend.lab.domain.print.entity.dto.resp.PrintResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/print")
@Tag(name = "[회원/공인중개사] 프린트 설정")
public class AgentPrintController {

  private final AgentPrintFacade agentPrintFacade;

  @Operation(summary = "프린트 설정 조회")
  @GetMapping
  public PrintResp getPrint() {
    return agentPrintFacade.get(getUserId());
  }

  @PostMapping("/image")
  public void updateImage(
      @RequestBody PrintUpdateReq req
  ) {
    agentPrintFacade.imageUpdate(req, getUserId());
  }

  @Operation(summary = "프린트 페이지 문구 설정")
  @PostMapping("/title")
  public void updateTitle(
      @RequestBody PrintUpdateReq req
  ) {
    agentPrintFacade.titleUpdate(req, getUserId());
  }
}
