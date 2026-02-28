package com.backend.lab.api.admin.organization.user.facade;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.api.admin.organization.user.dto.req.AdminUserCreateReq;
import com.backend.lab.api.admin.organization.user.dto.req.AdminUserUpdateReq;
import com.backend.lab.api.admin.organization.user.dto.resp.AdminActiveResp;
import com.backend.lab.common.auth.session.AdminSessionManager;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.req.AdminCreateReq;
import com.backend.lab.domain.admin.core.entity.dto.req.AdminUpdateReq;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.organization.department.service.DepartmentService;
import com.backend.lab.domain.admin.organization.jobGrade.entity.JobGrade;
import com.backend.lab.domain.admin.organization.jobGrade.service.JobGradeService;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.service.PermissionService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserFacade {

  private final AdminService adminService;
  private final AdminGlobalFacade adminCommonFacade;
  private final DepartmentService departmentService;
  private final JobGradeService jobGradeService;
  private final UploadFileService uploadFileService;
  private final PermissionService permissionService;

  private final AdminSessionManager adminSessionManager;

  @Transactional(readOnly = true)
  public PageResp<AdminActiveResp> search(SearchAdminOptions options) {
    Page<Admin> page = adminService.search(options);
    List<AdminGlobalResp> admins = page.stream()
        .map(adminCommonFacade::adminGlobalResp)
        .toList();

    List<Long> actives = adminSessionManager.getActives();
    List<AdminActiveResp> data = admins.stream()
        .map(a -> AdminActiveResp.builder()
            .adminGlobalResp(a)
            .isActive(actives.contains(a.getAdmin().getId()))
            .build())
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional(readOnly = true)
  public AdminGlobalResp getById(Long adminId) {
    Admin admin = adminService.getById(adminId);
    return adminCommonFacade.adminGlobalResp(admin);
  }

  @Transactional
  public void create(AdminUserCreateReq req) {

    AdminCreateReq createReq = req.getAdminCreateReq();
    if (createReq.getProfileImageId() != null) {
      createReq.setProfileImage(uploadFileService.getById(createReq.getProfileImageId()));
    }
    Admin admin = adminService.create(createReq);

    if (req.getDepartmentId() != null) {
      Department department = departmentService.getById(req.getDepartmentId());
      admin.setDepartment(department);
    }

    if (req.getJobGradeId() != null) {
      JobGrade jobGrade = jobGradeService.getById(req.getJobGradeId());
      admin.setJobGrade(jobGrade);
    }

    if (req.getPermissionId() != null) {
      Permission permission = permissionService.getById(req.getPermissionId());
      admin.setPermission(permission);
    }
  }

  @Transactional
  public void update(Long adminId, AdminUserUpdateReq req) {

    AdminUpdateReq updateReq = req.getAdminUpdateReq();
    if (updateReq.getProfileImageId() != null) {
      updateReq.setProfileImage(uploadFileService.getById(updateReq.getProfileImageId()));
    }

    Admin admin = adminService.getById(adminId);
    adminService.update(adminId, updateReq);

    if (req.getDepartmentId() != null) {
      Department department = departmentService.getById(req.getDepartmentId());
      admin.setDepartment(department);
    }

    if (req.getJobGradeId() != null) {
      JobGrade jobGrade = jobGradeService.getById(req.getJobGradeId());
      admin.setJobGrade(jobGrade);
    }

    if (req.getPermissionId() != null) {
      Permission permission = permissionService.getById(req.getPermissionId());
      admin.setPermission(permission);
    }
  }

  @Transactional
  public void delete(Long adminId) {
    adminService.delete(adminId);
  }
}
