package com.backend.lab.api.admin.global.facade;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import com.backend.lab.domain.admin.audit.accessLog.service.AdminAccessLogService;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.core.service.DeletedAdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.organization.department.service.DepartmentService;
import com.backend.lab.domain.admin.organization.jobGrade.service.JobGradeService;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.service.PermissionService;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGlobalFacade {

  private final AdminService adminService;
  private final DeletedAdminService deletedAdminService;
  private final DepartmentService departmentService;
  private final JobGradeService jobGradeService;
  private final AdminAccessLogService adminAccessLogService;
  private final PermissionService permissionService;
  private final UploadFileService uploadFileService;
  private final PropertyService propertyService;


  @Transactional(readOnly = true)
  public AdminGlobalResp adminGlobalResp(Admin admin) {

    AdminAccessLog accessLog = adminAccessLogService.getLatestLoginByAdminId(admin.getId());

    Department department = admin.getDepartment();
    Permission permission = admin.getPermission();

    return AdminGlobalResp.builder()
        .admin(adminService.adminResp(admin, uploadFileService.uploadFileResp(admin.getProfileImage())))
        .propertyCount(propertyService.countByAdminId(admin.getId()))
        .department(departmentService.departmentResp(department))
        .jobGrade(jobGradeService.jobGradeResp(admin.getJobGrade()))
        .lastLog(adminAccessLogService.adminAccessLogResp(accessLog))
        .permission(permissionService.permissionResp(permission))
        .build();
  }

  @Transactional(readOnly = true)
  public AdminGlobalResp adminGlobalResp(DeletedAdmin admin) {

    AdminAccessLog accessLog = adminAccessLogService.getLatestLoginByAdminId(admin.getId());

    Department department = admin.getDepartment();
    Permission permission = admin.getPermission();

    return AdminGlobalResp.builder()
        .admin(deletedAdminService.adminResp(admin,uploadFileService.uploadFileResp(admin.getProfileImage())))
        .propertyCount(propertyService.countByAdminId(admin.getId()))
        .department(departmentService.departmentResp(department))
        .jobGrade(jobGradeService.jobGradeResp(admin.getJobGrade()))
        .lastLog(adminAccessLogService.adminAccessLogResp(accessLog))
        .permission(permissionService.permissionResp(permission))
        .build();
  }
}
