package com.backend.lab.api.admin.organization.department.facade;

import com.backend.lab.api.admin.organization.department.dto.req.DepartmentAssignReq;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentCreateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentRerankReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentUpdateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.resp.DepartmentResp;
import com.backend.lab.domain.admin.organization.department.service.DepartmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDepartmentFacade {

  private final DepartmentService departmentService;
  private final AdminService adminService;

  public ListResp<DepartmentResp> gets() {
    List<DepartmentResp> data = departmentService.gets().stream()
        .map(departmentService::departmentResp)
        .toList();
    return new ListResp<>(data);
  }

  public DepartmentResp getById(Long departmentId) {
    Department department = departmentService.getById(departmentId);
    return departmentService.departmentResp(department);
  }

  @Transactional
  public void create(DepartmentCreateReq req) {
    departmentService.create(req);
  }

  @Transactional
  public void assign(DepartmentAssignReq req) {
    Long departmentId = req.getDepartmentId();

    Admin admin = adminService.getById(req.getAdminId());

    Department newDepartment = null;
    if (departmentId != null) {
      newDepartment = departmentService.getById(departmentId);
    }

    admin.setDepartment(newDepartment);
  }

  @Transactional
  public void rerank(DepartmentRerankReq req) {
    departmentService.rerank(req);
  }

  @Transactional
  public void update(Long id, DepartmentUpdateReq req) {
    departmentService.update(id, req);
  }

  @Transactional
  public void delete(Long id) {

    List<Admin> assignedAdmins = adminService.getsByDepartmentId(id);
    Department department = departmentService.getById(id);

    Department next = null;
    Long nextId = department.getMigrationDepartmentId();

    if (nextId != null) {
      next = departmentService.getById(nextId);
    }

    for (Admin admin : assignedAdmins) {
      admin.setDepartment(next);
    }

    departmentService.delete(id);
  }
}
