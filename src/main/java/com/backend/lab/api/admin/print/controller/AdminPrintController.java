package com.backend.lab.api.admin.print.controller;

import com.backend.lab.api.admin.print.facade.AdminPrintFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.domain.print.entity.dto.req.PrintUpdateReq;
import com.backend.lab.domain.print.entity.dto.resp.PrintResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/print")
@Tag(name = "[관리자] 프린트 설정")
public class AdminPrintController {

  private final AdminPrintFacade adminPrintFacade;

  @Operation(summary = "프린트 설정 조회")
  @GetMapping
  public PrintResp getPrint() {
    return adminPrintFacade.get();
  }

  @PreAuthorize("hasAuthority('MANAGE_PRINT')")
  @Operation(summary = "프린트 이미지 설정")
  @PostMapping("/image")
  public void updateImage(
      @RequestBody PrintUpdateReq req
  ) {
    adminPrintFacade.imageUpdate(req);
  }

  @PreAuthorize("hasAuthority('MANAGE_PRINT')")
  @Operation(summary = "프린트 페이지 문구 설정")
  @PostMapping("/title")
  public void updateTitle(
      @RequestBody PrintUpdateReq req
  ) {
    adminPrintFacade.titleUpdate(req);
  }
}
