package com.backend.lab.api.member.agent.promotionText.controller;


import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.agent.promotionText.facade.AgentPromotionTextFacade;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.promotionText.entity.dto.req.PromotionTextUpdateReq;
import com.backend.lab.domain.promotionText.entity.dto.resp.PromotionTextFieldResp;
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
@RequestMapping("/agent/promotion")
@Tag(name = "[회원/공인중개사] 홍보문구 설정")
public class AgentPromotionTextController {

  private final AgentPromotionTextFacade agentPromotionTextFacade;

  @Operation(summary = "홍보 문구 조회")
  @GetMapping
  public ListResp<PromotionTextFieldResp> get(
  ) {
    return agentPromotionTextFacade.getPromotionText(getUserId());
  }


  @Operation(summary = "홍보 문구 설정")
  @PostMapping
  public void create(
      @RequestBody PromotionTextUpdateReq req
  ) {
    agentPromotionTextFacade.create(req,getUserId());
  }
}
