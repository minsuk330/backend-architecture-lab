package com.backend.lab.api.admin.session.controller;

import com.backend.lab.api.admin.global.dto.resp.AdminDashboardInfoResp;
import com.backend.lab.api.admin.session.dto.req.GetAdminSessionOptions;
import com.backend.lab.api.admin.session.facade.AdminSessionFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.auth.session.AdminSessionManager;
import com.backend.lab.common.entity.dto.resp.ListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/session")
@RequiredArgsConstructor
@Tag(name = "[관리자] 접속 관리")
public class AdminSessionController {

  private final AdminSessionFacade adminSessionFacade;
  private final AdminSessionManager adminSessionManager;

  @Operation(summary = "모든 관리자/직원 접속 강제 종료")
  @PostMapping("/logout/all")
  public void logoutAll() {
    adminSessionManager.truncate();
  }

  @Operation(summary = "모든 관리자/직원 조회 (접속 여부 포함)")
  @GetMapping("/active")
  public ListResp<AdminDashboardInfoResp> getAllAdmins(
      @ModelAttribute GetAdminSessionOptions options
  ) {
    List<Long> activeAdminIds = adminSessionManager.getActives();
    return adminSessionFacade.getAllAdmins(activeAdminIds, options);
  }
}
