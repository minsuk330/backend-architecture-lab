package com.backend.lab.api.admin.organization.user.controller;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.organization.user.facade.AdminDeletedUserFacade;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/organization/user/deleted")
@RequiredArgsConstructor
@Tag(name = "[관리자] 삭제된 계정 관리")
public class AdminDeletedUserController {

  private final AdminDeletedUserFacade adminDeletedUserFacade;

  @Operation(summary = "계정 검색")
  @GetMapping
  public PageResp<AdminGlobalResp> search(
      @ModelAttribute SearchAdminOptions options
  ) {
    return adminDeletedUserFacade.search(options);
  }

  @Operation(summary = "계정 복구")
  @GetMapping("/restore/{id}")
  public void restore(
      @PathVariable("id") Long id
  ) {
    adminDeletedUserFacade.restore(id);
  }

  @Operation(summary = "계정 영구 삭제")
  @DeleteMapping("/remove/{id}")
  public void remove(
      @PathVariable("id") Long id
  ) {
    adminDeletedUserFacade.remove(id);
  }
}
