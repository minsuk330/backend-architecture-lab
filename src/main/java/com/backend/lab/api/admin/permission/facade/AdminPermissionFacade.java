package com.backend.lab.api.admin.permission.facade;

import com.backend.lab.api.admin.permission.dto.req.PermissionAssignReq;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.service.DepartmentService;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionCreateReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionRerankReq;
import com.backend.lab.domain.admin.permission.entity.dto.req.PermissionUpdateReq;
import com.backend.lab.domain.admin.permission.entity.dto.resp.PermissionResp;
import com.backend.lab.domain.admin.permission.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPermissionFacade {

  private final DepartmentService departmentService;
  private final PermissionService permissionService;
  private final AdminService adminService;

  public ListResp<PermissionResp> gets() {
    List<PermissionResp> data = permissionService.gets().stream()
        .map(permissionService::permissionResp)
        .toList();
    return new ListResp<>(data);
  }

  public PermissionResp getById(Long id) {
    Permission permission = permissionService.getById(id);
    return permissionService.permissionResp(permission);
  }

  @Transactional
  public void create(PermissionCreateReq req) {
    permissionService.create(req);
  }

  @Transactional
  public void update(Long permissionId, PermissionUpdateReq req) {
    permissionService.update(permissionId, req);
  }

  @Transactional
  public void assign(PermissionAssignReq req) {
    Long permissionId = req.getPermissionId();
    Long adminId = req.getAdminId();

    Admin admin = adminService.getById(adminId);
    Permission permission = permissionService.getById(permissionId);

    admin.setPermission(permission);
  }

  @Transactional
  public void rerank(PermissionRerankReq req) {
    permissionService.rerank(req);
  }


  @Transactional
  public void delete(Long permissionId) {
    Permission permission = permissionService.getById(permissionId);

    Long nextId = permission.getMigrationPermissionId();
    Permission next = null;
    if (nextId != null) {
      next = permissionService.getById(nextId);
    }

    List<Admin> admins = adminService.getsByPermissionId(permissionId);
    for (Admin admin : admins) {
      admin.setPermission(next);
    }

    permissionService.delete(permissionId);
  }
}
