package com.backend.lab.api.admin.accessLog.controller;

import com.backend.lab.api.admin.accessLog.dto.resp.AdminAccessLogApiResp;
import com.backend.lab.api.admin.accessLog.dto.resp.MemberAccessLogApiResp;
import com.backend.lab.api.admin.accessLog.facade.AdminAccessLogFacade;
import com.backend.lab.api.admin.accessLog.facade.MemberAccessLogFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.audit.accessLog.entity.dto.req.GetAdminAccessLogOptions;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.req.GetMemberAccessLogOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/access-log")
@RequiredArgsConstructor
@Tag(name = "[관리자] 로그인 기록")
public class AdminAccessLogController {

  private final AdminAccessLogFacade adminAccessLogFacade;
  private final MemberAccessLogFacade memberAccessLogFacade;

  // get logs list////
  @Operation(summary = "관리자 로그인 기록 조회")
  @GetMapping("/admin")
  public PageResp<AdminAccessLogApiResp> search(
      @ModelAttribute GetAdminAccessLogOptions options
  ) {
    return adminAccessLogFacade.search(options);
  }

  @Operation(summary = "회원 로그인 기록 조회")
  @GetMapping("/member")
  public PageResp<MemberAccessLogApiResp> search(
      @ModelAttribute GetMemberAccessLogOptions options
  ) {
    return memberAccessLogFacade.search(options);
  }

}
