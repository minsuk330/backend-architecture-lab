package com.backend.lab.api.admin.member.agent.controller;

import com.backend.lab.api.admin.member.agent.facade.AdminAgentFacade;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentListOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentSearchOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAssignTicketReq;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminUpdateAgentItemReq;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentFullResp;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentSearchResp;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentSimpleResp;
import com.backend.lab.api.admin.member.agent.facade.dto.resp.AdminAgentTicketsResp;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/agent")
@Tag(name = "[관리자/회원관리] 공인중개사")
public class AdminAgentController {

  private final AdminAgentFacade adminAgentFacade;
  private final AdminPropertyFacade adminPropertyFacade;

  @Operation(summary = "공인중개사 검색")
  @GetMapping
  public PageResp<AdminAgentSearchResp> search(
      @ModelAttribute AdminAgentSearchOptions options
  ) {
    return adminAgentFacade.search(options);
  }

  @Operation(summary = "활성 공인중개사 목록 (삭제되지 않은 것만, 검색 및 페이지네이션 지원)")
  @GetMapping("/list")
  public PageResp<AdminAgentSimpleResp> listActiveAgents(
      @ParameterObject @ModelAttribute AdminAgentListOptions options
  ) {
    return adminAgentFacade.listActiveAgents(options);
  }

  @Operation(summary = "공인중개사 상세 조회")
  @GetMapping("/{id}")
  public AdminAgentFullResp getById(
      @PathVariable("id") Long id
  ) {
    return adminAgentFacade.getById(id);
  }

  @Operation(summary = "등록된 매매 물건")
  @GetMapping("/{id}/property")
  public PageResp<PropertySearchResp> getProperties(
      @ParameterObject @ModelAttribute PageOptions options,
      @PathVariable Long id
  ) {
    return adminPropertyFacade.getsByExclusiveAgentId(id, options);
  }

  @Operation(summary = "광고중인 매물")
  @GetMapping("/{id}/adv")
  public PageResp<PropertySearchResp> getAdvProperties(
      @ParameterObject @ModelAttribute PageOptions options,
      @PathVariable Long id
  ) {
    return adminPropertyFacade.getsByAgentAdv(id, options);
  }

  @Operation(summary = "이용권 조회")
  @GetMapping("/{id}/tickets")
  public PageResp<AdminAgentTicketsResp> getTickets(
      @PathVariable Long id,
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return adminAgentFacade.getTickets(id, options);
  }

  @Operation(summary = "공인중개사에게 이용권 직접 할당")
  @PostMapping("/{id}/tickets")
  public void assignTicket(
      @PathVariable Long id,
      @Valid @RequestBody AdminAssignTicketReq req
  ) {
    adminAgentFacade.assignTicket(id, req);
  }

  @Operation(summary = "공인중개사 이용권 항목 수정 (연락처 열람권, 매물 광고권)")
  @PatchMapping("/{id}/item")
  public void updateAgentItem(
      @PathVariable Long id,
      @Valid @RequestBody AdminUpdateAgentItemReq req
  ) {
    adminAgentFacade.updateAgentItem(id, req);
  }
}
