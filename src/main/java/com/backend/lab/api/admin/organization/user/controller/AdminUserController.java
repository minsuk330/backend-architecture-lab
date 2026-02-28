package com.backend.lab.api.admin.organization.user.controller;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.organization.user.dto.req.AdminUserCreateReq;
import com.backend.lab.api.admin.organization.user.dto.req.AdminUserUpdateReq;
import com.backend.lab.api.admin.organization.user.dto.resp.AdminActiveResp;
import com.backend.lab.api.admin.organization.user.facade.AdminUserFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/organization/user")
@RequiredArgsConstructor
@Tag(name = "[관리자] 계정 관리")
public class AdminUserController {

  private final AdminUserFacade adminUserFacade;

  @Operation(summary = "계정 검색")
  @GetMapping
  public PageResp<AdminActiveResp> search(
      @ModelAttribute SearchAdminOptions options
  ) {
    return adminUserFacade.search(options);
  }

  @Operation(summary = "계정 상세 조회")
  @GetMapping("/{adminId}")
  public AdminGlobalResp getById(
      @PathVariable("adminId") Long adminId
  ) {
    return adminUserFacade.getById(adminId);
  }

  @Operation(summary = "계정 생성")
  @PostMapping
  public void create(
      @RequestBody AdminUserCreateReq req
  ) {
    adminUserFacade.create(req);
  }

  @Operation(summary = "계정 수정")
  @PutMapping("/{adminId}")
  public void update(
      @PathVariable("adminId") Long adminId,
      @RequestBody AdminUserUpdateReq req
  ) {
    adminUserFacade.update(adminId, req);
  }

  @Operation(summary = "계정 삭제")
  @DeleteMapping("/{adminId}")
  public void delete(
      @PathVariable("adminId") Long adminId
  ) {
    adminUserFacade.delete(adminId);
  }
}
