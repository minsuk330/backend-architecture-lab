package com.backend.lab.api.member.agent.property.controller;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestAgentReq;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.agent.property.facade.AgentPropertyFacade;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.auth.annotation.RequireAgentRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireAgentRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent/property")
@Tag(name = "[회원/공인중개사] 공인중개사 매물")
public class AgentPropertyController {

  private final AgentPropertyFacade agentPropertyFacade;

  @Operation(summary = "내 매물 조회")
  @GetMapping("/list")
  public PageResp<PropertyListResp> list(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return agentPropertyFacade.getsByExclusiveAgentId(AuthUtil.getUserId(), options);
  }
  @Operation(summary = "매물 목록 조회")
  @GetMapping("/search")
  public PageResp<PropertySearchResp> search(
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute SearchPropertyOptions options
  ) {
    return agentPropertyFacade.search(options, bigCategoryIds, smallCategoryIds);
  }

  @Operation(summary = "매물중개담당광고 조회")
  @GetMapping("/adv")
  public PageResp<PropertyListResp> advList(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return agentPropertyFacade.getsByAgentAdv(AuthUtil.getUserId(), options);
  }

  @Operation(summary = "공인중개사 매물 의뢰")
  @PostMapping("/request")
  public void createReq(
      @RequestBody PropertyRequestAgentReq req
  ) {
    agentPropertyFacade.createReq(req, AuthUtil.getUserId());
  }

  @Operation(summary = "매물 의뢰 내역")
  @GetMapping("/request/list")
  public PageResp<PropertyRequestAgentResp> getsRequest(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return agentPropertyFacade.getsRequest(AuthUtil.getUserId(), options);
  }


}
