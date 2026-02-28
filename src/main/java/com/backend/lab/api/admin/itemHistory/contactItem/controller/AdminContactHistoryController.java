package com.backend.lab.api.admin.itemHistory.contactItem.controller;

import com.backend.lab.api.admin.itemHistory.contactItem.dto.req.AdminContactCountSearchOptions;
import com.backend.lab.api.admin.itemHistory.contactItem.dto.resp.AdminContactCountResp;
import com.backend.lab.api.admin.itemHistory.contactItem.facade.AdminContactHistoryFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactItemHistoryResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('MANAGE_USAGE_HISTORY')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/contact-history")
@Tag(name = "[관리자/사용 내역] 매도자 연락처 열람")
public class AdminContactHistoryController {

  private final AdminContactHistoryFacade adminContactHistoryFacade;

  @Operation(summary = "검색")
  @GetMapping("/list")
  public PageResp<ContactItemHistoryResp> search(
      @ModelAttribute ContactHistorySearchOptions options
  ) {
    return adminContactHistoryFacade.search(options);
  }

  @Operation(summary = "사업자별 열람횟수 조회")
  @GetMapping("/count")
  public PageResp<AdminContactCountResp> count(
      @ModelAttribute AdminContactCountSearchOptions options
  ) {
    return adminContactHistoryFacade.count(options);
  }
}
