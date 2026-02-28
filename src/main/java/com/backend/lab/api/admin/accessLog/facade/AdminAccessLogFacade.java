package com.backend.lab.api.admin.accessLog.facade;

import com.backend.lab.domain.admin.audit.accessLog.entity.dto.req.GetAdminAccessLogOptions;
import com.backend.lab.api.admin.accessLog.dto.resp.AdminAccessLogApiResp;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import com.backend.lab.domain.admin.audit.accessLog.service.AdminAccessLogService;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAccessLogFacade {

  private final AdminAccessLogService accessLogService;
  private final AdminGlobalFacade adminCommonFacade;
  private final AdminService adminService;

  public PageResp<AdminAccessLogApiResp> search(GetAdminAccessLogOptions options) {

    Page<AdminAccessLog> page = accessLogService.search(options);

    List<AdminAccessLogApiResp> data = page.getContent().stream()
        .map(accessLog -> {
          try {
            Admin admin = adminService.getById(accessLog.getAdminId());
            return AdminAccessLogApiResp.builder()
                .admin(adminCommonFacade.adminGlobalResp(admin))
                .log(accessLogService.adminAccessLogResp(accessLog))
                .build();
          } catch (Exception e) {
            // Admin이 삭제된 경우 null 처리
            return AdminAccessLogApiResp.builder()
                .admin(null)
                .log(accessLogService.adminAccessLogResp(accessLog))
                .build();
          }
        })
        .toList();

    return new PageResp<>(page, data);
  }
}
