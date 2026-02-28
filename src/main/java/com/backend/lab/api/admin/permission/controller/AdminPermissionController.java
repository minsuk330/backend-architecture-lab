package com.backend.lab.api.admin.permission.controller;

import com.backend.lab.api.admin.permission.dto.req.PermissionAssignReq;
import com.backend.lab.api.admin.permission.facade.AdminPermissionFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionCreateReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionRerankReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionUpdateReq;
import com.backend.lab.domain.admin.permission.entity.dto.resp.PermissionResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@Tag(name = "[관리자] 권한 관리")
public class AdminPermissionController {

  private final AdminPermissionFacade adminPermissionFacade;

  @Operation(summary = "권한 목록 조회")
  @GetMapping
  public ListResp<PermissionResp> gets() {
    return adminPermissionFacade.gets();
  }

  @Operation(summary = "권한 상세 조회")
  @GetMapping("/{permissionId}")
  public PermissionResp getById(
      @PathVariable("permissionId") Long permissionId
  ) {
    return adminPermissionFacade.getById(permissionId);
  }

  @Operation(summary = "권한 생성")
  @PostMapping
  public void create(
      @RequestBody PermissionCreateReq req
  ) {
    adminPermissionFacade.create(req);
  }

  @Operation(summary = "권한 수정")
  @PutMapping("/{permissionId}")
  public void update(
      @PathVariable("permissionId") Long permissionId,
      @RequestBody @Valid PermissionUpdateReq req
  ) {
    adminPermissionFacade.update(permissionId, req);
  }

  @Operation(summary = "권한 할당")
  @PostMapping("/assign")
  public void assign(
      @RequestBody PermissionAssignReq req
  ) {
    adminPermissionFacade.assign(req);
  }

  @Operation(summary = "권한 우선순위 변경")
  @PostMapping("/rerank")
  public void rerank(
      @RequestBody PermissionRerankReq req
  ) {
    adminPermissionFacade.rerank(req);
  }

  @Operation(summary = "권한 삭제")
  @DeleteMapping("/{permissionId}")
  public void delete(
      @PathVariable("permissionId") Long permissionId
  ) {
    adminPermissionFacade.delete(permissionId);
  }
}
