package com.backend.lab.api.member.agent.payment.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentTicketsResp;
import com.backend.lab.api.member.agent.payment.facade.item.AgentItemFacade;
import com.backend.lab.api.member.agent.payment.facade.item.dto.AgentMyItemResp;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
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
@RequestMapping("/agent/item")
@RequiredArgsConstructor
@Tag(name = "[회원/공인중개사] 아이템 구매 관련")
public class AgentItemController {

  private final AgentItemFacade agentItemFacade;

  // get my items\
  @Operation(summary = "내 아이템 조회")
  @GetMapping("/my")
  public AgentMyItemResp getMyItem() {
    return agentItemFacade.getMyItem(getUserId());
  }

  // get can purchase item list
  @Operation(summary = "구매 가능 아이템 조회")
  @GetMapping
  public ListResp<ProductResp> getProducts() {
    return agentItemFacade.getProducts(getUserId());
  }

  @Operation(summary = "이용권 조회")
  @GetMapping("/tickets")
  public PageResp<AdminAgentTicketsResp> getTickets(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return agentItemFacade.getTickets(getUserId(), options);
  }
}
