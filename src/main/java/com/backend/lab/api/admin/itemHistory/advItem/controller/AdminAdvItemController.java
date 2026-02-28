package com.backend.lab.api.admin.itemHistory.advItem.controller;

import com.backend.lab.api.admin.itemHistory.advItem.facade.AdminAdvItemFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseItemHistoryResp;
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
@RequestMapping("/admin/adv-history")
@Tag(name = "[관리자/사용 내역] 매도중개담당광고")
public class AdminAdvItemController {

  private final AdminAdvItemFacade adminAdvItemFacade;

  @Operation(summary = "검색")
  @GetMapping
  public PageResp<AdvertiseItemHistoryResp> search(
      @ModelAttribute AdvertiseHistorySearchOptions options
  ) {
    return adminAdvItemFacade.search(options);
  }
}
