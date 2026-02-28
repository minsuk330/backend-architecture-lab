package com.backend.lab.api.admin.me.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.global.dto.resp.AdminGlobalResp;
import com.backend.lab.api.admin.me.facade.AdminMeFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/me")
@RequiredArgsConstructor
@Tag(name = "[관리자] 내 정보 관리")
public class AdminMeController {

  private final AdminMeFacade adminMeFacade;

  @Operation(summary = "내 정보 조회")
  @GetMapping
  public AdminGlobalResp getMyInfo() {
    return adminMeFacade.me(getUserId());
  }
}
