package com.backend.lab.api.admin.auth.facade;

import com.backend.lab.api.admin.auth.dto.req.LocalLoginReq;
import com.backend.lab.api.admin.auth.dto.req.ResetPasswordReq;
import com.backend.lab.api.admin.auth.dto.resp.AdminAuthenticationToken;
import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.mail.MailManager;
import com.backend.lab.common.util.StringUtil;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AgentType;
import com.backend.lab.domain.admin.audit.accessLog.service.AdminAccessLogService;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.vo.PermissionAuthority;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthFacade {

  private final AdminService adminService;
  private final AdminAccessLogService adminAccessLogService;
  private final AdminGlobalFacade adminCommonFacade;
  private final MailManager mailManager;

  private final PasswordEncoder passwordEncoder;

  public AdminAuthenticationToken login(LocalLoginReq req, String ipAddress, AgentType agentType) {
    try {
      Admin admin = adminService.getByEmail(req.getEmail());

      if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
        throw new BusinessException(ErrorCode.LOGIN_FAILED);
      }

      Set<String> roles = new HashSet<>();
      roles.addAll(Arrays.asList(admin.getRole().getRoleName()));

      AdminRole role = admin.getRole();
      if (role.equals(AdminRole.ADMIN)) {
        roles.addAll(Arrays.stream(PermissionAuthority.values())
            .map(PermissionAuthority::name)
            .toList());
      }

      Permission permission = admin.getPermission();
      if (permission != null) {
        roles.addAll(permission.getAuthorities());
      }

      List<SimpleGrantedAuthority> authorities = roles.stream()
          .map(SimpleGrantedAuthority::new)
          .toList();
      AdminAuthenticationToken authenticationToken = new AdminAuthenticationToken(
          admin.getId(),
          null,
          authorities
      );

      adminAccessLogService.createLoginLog(admin.getId(), ipAddress, agentType);

      authenticationToken.setAdmin(adminCommonFacade.adminGlobalResp(admin));
      return authenticationToken;
    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      log.error("Login failed for email: {}, error: ", req.getEmail(), e);
      throw new BusinessException(ErrorCode.LOGIN_FAILED);
    }
  }

  @Transactional
  public void sendResetPasswordEmail(ResetPasswordReq req) {
    String email = req.getEmail();
    Admin admin = adminService.getByEmail(email);
    if (admin == null) {
      throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND);
    }

    String newPassword = StringUtil.generateRandomString(8);
    String encodedPassword = passwordEncoder.encode(newPassword);
    admin.setPassword(encodedPassword);
    mailManager.sendResetPasswordMail(email, newPassword);
  }

  public AdminGlobalResp adminResp(Long id) {
    Admin admin = adminService.getById(id);
    return adminCommonFacade.adminGlobalResp(admin);
  }

  @Transactional
  public void logout(Long userId, String clientIp, AgentType agentType) {
    adminAccessLogService.createLogoutLog(userId, clientIp, agentType);
  }
}
