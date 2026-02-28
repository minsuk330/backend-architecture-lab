package com.backend.lab.api.admin.customer.controller;


import static com.backend.lab.common.util.NetworkUtil.getClientIp;

import com.backend.lab.api.admin.customer.facade.AdminCustomerFacade;
import com.backend.lab.api.admin.customer.facade.dto.AdminCustomerResp;
import com.backend.lab.api.admin.customer.facade.dto.CustomerDeleteResp;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/customer")
@RequiredArgsConstructor
@Tag(name = "[관리자] 고객 목록")
public class AdminCustomerController {

  private final AdminCustomerFacade adminCustomerFacade;

  @Operation(summary = "고객 검색")
  @GetMapping
  public PageResp<AdminCustomerResp> search(
      @ModelAttribute AdminCustomerSearchOptions options
  ) {
    return adminCustomerFacade.search(options);
  }

  // 고객 생성
  @PreAuthorize("hasAuthority('MANAGE_CUSTOMER')")
  @Operation(summary = "고객 생성")
  @PostMapping
  public AdminCustomerResp create(
      @RequestBody AdminCustomerDTO req,
      HttpServletRequest request
  ) {
    return adminCustomerFacade.create(req, AuthUtil.getUserId(),getClientIp(request));
  }

  @PreAuthorize("hasAuthority('MANAGE_CUSTOMER')")
  @Operation(summary = "고객 수정")
  @PutMapping("/{id}")
  public AdminCustomerResp create(
      @PathVariable("id") Long id,
      @RequestBody AdminCustomerDTO req,
      HttpServletRequest request
  ) {
    return adminCustomerFacade.update(id, req, AuthUtil.getUserId(),getClientIp(request));
  }

  // 고객 수정
  @PreAuthorize("hasAuthority('MANAGE_CUSTOMER')")
  @Operation(summary = "고객 삭제")
  @DeleteMapping("/{id}")
  public CustomerDeleteResp delete(
      @PathVariable("id") Long id) {
    return adminCustomerFacade.delete(id);
  }
}
