package com.backend.lab.domain.admin.audit.accessLog.service;

import com.backend.lab.domain.admin.audit.accessLog.entity.dto.req.GetAdminAccessLogOptions;
import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import com.backend.lab.domain.admin.audit.accessLog.entity.dto.resp.AdminAccessLogResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AdminAccessLogType;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AgentType;
import com.backend.lab.domain.admin.audit.accessLog.repository.AdminAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAccessLogService {

  private final AdminAccessLogRepository adminAccessLogRepository;

  public Page<AdminAccessLog> search(GetAdminAccessLogOptions options) {
    return adminAccessLogRepository.search(options);
  }

  @Transactional
  public void createLoginLog(Long adminId, String ipAddress, AgentType agentType) {
    adminAccessLogRepository.save(
        AdminAccessLog.builder()
            .adminId(adminId)
            .agentType(agentType)
            .ipAddress(ipAddress)
            .type(AdminAccessLogType.LOGIN)
            .build()
    );
  }

  @Transactional
  public void createLogoutLog(Long adminId, String ipAddress, AgentType agentType) {
    adminAccessLogRepository.save(
        AdminAccessLog.builder()
            .adminId(adminId)
            .agentType(agentType)
            .ipAddress(ipAddress)
            .type(AdminAccessLogType.LOGOUT)
            .build()
    );
  }

  public AdminAccessLog getLatestLoginByAdminId(Long adminId) {
    return adminAccessLogRepository.findTopByAdminIdAndTypeOrderByCreatedAtDesc(adminId, AdminAccessLogType.LOGIN);
  }

  public Page<AdminAccessLog> getsByAdminId(Long adminId, Pageable pageable) {
    return adminAccessLogRepository.findAllByAdminId(adminId, pageable);
  }

  public Page<AdminAccessLog> getsLatestLogsEachUser(Pageable pageable) {
    return adminAccessLogRepository.findLatestLogsEachUser(pageable);
  }

  public AdminAccessLogResp adminAccessLogResp(AdminAccessLog adminAccessLog) {

    if (adminAccessLog == null) {
      return null;
    }

    return AdminAccessLogResp.builder()
        .id(adminAccessLog.getId())
        .createdAt(adminAccessLog.getCreatedAt())

        .type(adminAccessLog.getType())
        .ipAddress(adminAccessLog.getIpAddress())
        .memberId(adminAccessLog.getAdminId())
        .agentType(adminAccessLog.getAgentType())
        .build();
  }
}
