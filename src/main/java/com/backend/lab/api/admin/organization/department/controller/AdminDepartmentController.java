package com.backend.lab.api.admin.organization.department.controller;

import com.backend.lab.api.admin.organization.department.dto.req.DepartmentAssignReq;
import com.backend.lab.api.admin.organization.department.facade.AdminDepartmentFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentCreateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentRerankReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentUpdateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.resp.DepartmentResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/admin/organization/department")
@RequiredArgsConstructor
@Tag(name = "[관리자] 부서 관리")
public class AdminDepartmentController {

  private final AdminDepartmentFacade adminOrganizationFacade;

  @Operation(summary = "부서 리스트 조회")
  @GetMapping
  public ListResp<DepartmentResp> getList() {
    return adminOrganizationFacade.gets();
  }

  @Operation(summary = "부서 상세 조회")
  @GetMapping("/{departmentId}")
  public DepartmentResp get(
      @PathVariable("departmentId") Long departmentId
  ) {
    return adminOrganizationFacade.getById(departmentId);
  }

  @Operation(summary = "부서 생성")
  @PostMapping
  public void create(
      @RequestBody DepartmentCreateReq req
  ) {
    adminOrganizationFacade.create(req);
  }

  @Operation(summary = "부서 할당")
  @PostMapping("/assign")
  public void assign(
      @RequestBody DepartmentAssignReq req
  ) {
    adminOrganizationFacade.assign(req);
  }

  @Operation(summary = "부서 우선순위 변경")
  @PostMapping("/rerank")
  public void rerank(
      @RequestBody DepartmentRerankReq req
  ) {
    adminOrganizationFacade.rerank(req);
  }

  @Operation(summary = "부서 수정")
  @PutMapping("/{departmentId}")
  public void update(
      @PathVariable("departmentId") Long departmentId,
      @RequestBody DepartmentUpdateReq req
  ) {
    adminOrganizationFacade.update(departmentId, req);
  }

  @Operation(summary = "부서 삭제")
  @DeleteMapping("/{departmentId}")
  public void delete(
      @PathVariable("departmentId") Long departmentId
  ) {
    adminOrganizationFacade.delete(departmentId);
  }
}
