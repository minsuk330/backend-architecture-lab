package com.backend.lab.api.admin.me.facade;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.global.facade.AdminGlobalFacade;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMeFacade {

  private final AdminService adminService;
  private final AdminGlobalFacade adminCommonFacade;

  public AdminGlobalResp me(Long memberId) {
    Admin admin = adminService.getById(memberId);
    return adminCommonFacade.adminGlobalResp(admin);
  }
}
