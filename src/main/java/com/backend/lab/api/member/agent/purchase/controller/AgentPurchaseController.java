package com.backend.lab.api.member.agent.purchase.controller;


import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.purchase.facade.AgentPurchaseFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.purchase.core.entity.dto.PurchaseResp;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequestMapping("/agent/purchase_history")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 결제 내역")
public class AgentPurchaseController {

  private final AgentPurchaseFacade agentPurchaseFacade;

  @GetMapping
  public PageResp<PurchaseResp> gets(
      @ParameterObject @ModelAttribute PageOptions pageOptions
  ) {
    return agentPurchaseFacade.gets(getUserId(), pageOptions);
  }
}
