package com.backend.lab.api.admin.member.buyer.controller;

import com.backend.lab.api.admin.member.buyer.facade.AdminBuyerFacade;
import com.backend.lab.api.admin.member.buyer.facade.dto.req.AdminBuyerSearchOptions;
import com.backend.lab.api.admin.member.buyer.facade.dto.resp.AdminBuyerFullResp;
import com.backend.lab.api.admin.member.buyer.facade.dto.resp.AdminBuyerSearchResp;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/buyer")
@Tag(name = "[관리자/회원관리] 매수자")
public class AdminBuyerController {

  private final AdminBuyerFacade adminBuyerFacade;

  @Operation(summary = "매수자 검색")
  @GetMapping
  public PageResp<AdminBuyerSearchResp> search(
      @ModelAttribute AdminBuyerSearchOptions options
  ) {
    return adminBuyerFacade.search(options);
  }

  @Operation(summary = "매수자 상세 조회")
  @GetMapping("/{id}")
  public AdminBuyerFullResp getById(
      @PathVariable("id") Long id
  ) {
    return adminBuyerFacade.getById(id);
  }
}
