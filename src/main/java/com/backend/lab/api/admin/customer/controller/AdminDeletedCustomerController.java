package com.backend.lab.api.admin.customer.controller;

import com.backend.lab.api.admin.customer.facade.AdminDeletedCustomerFacade;
import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/customer/deleted")
@RequiredArgsConstructor
@Tag(name = "[관리자] 삭제된 고객 목록")
public class AdminDeletedCustomerController {

  private final AdminDeletedCustomerFacade adminDeletedCustomerFacade;

  @Operation(summary = "고객 검색")
  @GetMapping
  public PageResp<MemberGlobalResp> search(
      @ModelAttribute AdminCustomerSearchOptions options
  ) {
    return adminDeletedCustomerFacade.search(options);
  }

  @PreAuthorize("hasAuthority('MANAGE_CUSTOMER')")
  @Operation(summary = "고객 복구")
  @GetMapping("/restore/{id}")
  public void restore(
      @PathVariable("id") Long id
  ) {
    adminDeletedCustomerFacade.restore(id);
  }

  @PreAuthorize("hasAuthority('MANAGE_CUSTOMER')")
  @Operation(summary = "고객 영구 삭제")
  @DeleteMapping("/remove/{id}")
  public void remove(
      @PathVariable("id") Long id
  ) {
    adminDeletedCustomerFacade.remove(id);
  }
}
