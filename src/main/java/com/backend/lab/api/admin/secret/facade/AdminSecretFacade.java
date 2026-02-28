package com.backend.lab.api.admin.secret.facade;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.entity.dto.req.SecretReq;
import com.backend.lab.domain.secret.entity.dto.resp.SecretResp;
import com.backend.lab.domain.secret.service.SecretService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminSecretFacade {

  private final SecretService secretService;
  private final AdminService adminService;
  private final PropertyService propertyService;

  @Transactional
  public void createOrUpdate(SecretReq req, Long propertyId, Long userId) {
    Property property = propertyService.getById(propertyId);
    Admin admin = adminService.getById(userId);
    secretService.create(req, property, admin);
  }

  public ListResp<SecretResp> getSecret(Long propertyId, Long adminId) {
    // propertyId null 체크
    if (propertyId == null) {
      return new ListResp<>(List.of());
    }

    // adminId null 체크
    if (adminId == null) {
      return new ListResp<>(List.of());
    }

    List<Secret> secrets = secretService.getsByProperty(propertyId);

    Admin admin = adminService.getById(adminId);
    Department department = admin.getDepartment();

    if (admin.getRole().equals(AdminRole.ADMIN)) {
      return new ListResp<>(
          secrets.stream()
              .map(secretService::secretResp)
              .toList()
      );
    }

    Permission permission = admin.getPermission();
    if (permission == null) {
      log.debug("관리자(id: {})에게 권한이 할당되지 않아 비밀메모 조회 불가", adminId);
      return new ListResp<>(List.of());
    }

    SecretMemoExposeLevel secretMemoExposeLevel = permission.getSecretMemoExposeLevel();
    if (secretMemoExposeLevel == null) {
      log.debug("관리자(id: {})에게 비밀메모 노출 권한이 설정되지 않음", adminId);
      return new ListResp<>(List.of());
    }

    List<SecretResp> data = secretService.filter(adminId, department, secretMemoExposeLevel, secrets).stream()
        .map(secretService::secretResp)
        .toList();

    return new ListResp<>(data);
  }

  @Transactional
  public void deleteSecret(Long propertyId, Long userId) {
    secretService.delete(propertyId, userId);
  }
}
