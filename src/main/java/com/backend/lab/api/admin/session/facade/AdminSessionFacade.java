package com.backend.lab.api.admin.session.facade;

import com.backend.lab.api.admin.global.dto.resp.AdminDashboardInfoResp;
import com.backend.lab.api.admin.global.dto.resp.AdminDashboardInfoResp.PropertyCount;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.api.admin.session.dto.req.GetAdminSessionOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLog;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyWorkLogService;
import com.backend.lab.domain.property.taskNote.entity.vo.WorkLogType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSessionFacade {

  private final AdminService adminService;
  private final PropertyService propertyService;
  private final PropertyWorkLogService propertyWorkLogService;
  private final AdminGlobalFacade adminCommonFacade;

  public ListResp<AdminDashboardInfoResp> getAllAdmins(List<Long> activeAdminIds,
      GetAdminSessionOptions options) {
    // 모든 관리자 조회 (접속 여부 관계없이)
    List<Admin> admins = adminService.gets();

    List<AdminDashboardInfoResp> data = new ArrayList<>();
    for (Admin admin : admins) {

      long total = propertyService.countByAdminId(admin.getId());

      LocalDate today = LocalDate.now();
      LocalDate yesterday = today.minusDays(1);
      today = today.plusDays(1);

      List<PropertyWorkLog> workLogs = propertyWorkLogService.getsByAdminIdTodayBetween(
          admin.getId(), yesterday.atStartOfDay(), today.atStartOfDay());

      long registerYesterday = 0L;
      long registerToday = 0L;
      long updateYesterday = 0L;
      long updateToday = 0L;
      long deleteYesterday = 0L;
      long deleteToday = 0L;
      for (PropertyWorkLog workLog : workLogs) {
        WorkLogType type = workLog.getWorkLogType();
        boolean isYesterday =
            workLog.getCreatedAt().isAfter(yesterday.atStartOfDay()) && workLog.getCreatedAt()
                .isBefore(yesterday.plusDays(1).atStartOfDay());
        switch (type) {
          case CREATED:
            if (isYesterday) {
              registerYesterday++;
            } else {
              registerToday++;
            }
            break;
          case UPDATED:
            if (isYesterday) {
              updateYesterday++;
            } else{
              updateToday++;
            }

            break;
          case DELETED:
            if (isYesterday) {
              deleteYesterday++;
            } else {
              deleteToday++;
            }
            break;
        }
      }

      // 접속 여부 확인
      boolean isActive = activeAdminIds.contains(admin.getId());
      
      data.add(AdminDashboardInfoResp.builder()
              .admin(adminCommonFacade.adminGlobalResp(admin))
              .isActive(isActive)  // 접속 여부 추가
              .property(
                  PropertyCount.builder()
                      .total(total)
                      .registerYesterday(registerYesterday)
                      .registerToday(registerToday)
                      .updateYesterday(updateYesterday)
                      .updateToday(updateToday)
                      .deleteYesterday(deleteYesterday)
                      .deleteToday(deleteToday)
                      .build()
              )
          .build());
    }
    return new ListResp<>(data);
  }

  // 현재 접속중인 유저 정보 가져오기
}
