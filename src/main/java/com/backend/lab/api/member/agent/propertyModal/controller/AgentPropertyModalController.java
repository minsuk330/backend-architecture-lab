package com.backend.lab.api.member.agent.propertyModal.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.propertyModal.facade.AgentPropertyModalFacade;
import com.backend.lab.api.member.agent.propertyModal.facade.dto.resp.AgentPropertyContactResp;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequestMapping("/agent/property")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 매도자 정보 열람 & 광고 등록")
public class AgentPropertyModalController {

  private final AgentPropertyModalFacade agentPropertyModalFacade;

  // 매도자 정보 열람
  //todo 여기에 업무일지 5개도 보여지게 해야 함
  @Operation(summary = "매도자 조회")
  @GetMapping("/customer/{propertyId}")
  public AgentPropertyContactResp getById(
      @PathVariable("propertyId") Long propertyId
  ) {
    return agentPropertyModalFacade.getCustomers(propertyId, getUserId());
  }

  // 매도자 정보 열람권 사용
  @Operation(summary = "매도자 정보 열람권 사용")
  @PostMapping("/customer/{propertyId}")
  public void view(
      @PathVariable("propertyId") Long propertyId
  ) {
    agentPropertyModalFacade.view(propertyId, getUserId());
  }

  // 광고 등록
  @Operation(summary = "광고 등록")
  @PostMapping("/adv/{propertyId}")
  public void addAdv(
      @PathVariable("propertyId") Long propertyId
  ) {
    agentPropertyModalFacade.addAdv(propertyId, getUserId());
  }
}
