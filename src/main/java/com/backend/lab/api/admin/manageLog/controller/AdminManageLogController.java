package com.backend.lab.api.admin.manageLog.controller;

import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailOptions;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailResp;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogResp;
import com.backend.lab.api.admin.manageLog.dto.SearchMemberWorkLogOptions;
import com.backend.lab.api.admin.manageLog.facade.AdminManageLogFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailsOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.SearchPropertyWorkLogOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogDetailResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/manage-log")
@RequiredArgsConstructor
@Tag(name = "[관리자] 작업/고객 관리이력")
public class AdminManageLogController {

  private final AdminManageLogFacade adminManageLogFacade;

  @Operation(summary = "매물작업이력 검색")
  @GetMapping("/property")
  public PageResp<PropertyWorkLogResp> search(
      @ParameterObject @ModelAttribute SearchPropertyWorkLogOptions options
  ) {
    return adminManageLogFacade.search(options);
  }

  @Operation(summary = "고객작업이력")
  @GetMapping("/member")
  public PageResp<MemberWorkLogResp> search(
      @ParameterObject @ModelAttribute SearchMemberWorkLogOptions options
  ) {
    return adminManageLogFacade.gets(options);
  }

  @Operation(summary = "고객 작업이력 상세내역")
  @GetMapping("/member-log")
  public PageResp<MemberWorkLogDetailResp> getDetails(
      @ParameterObject @ModelAttribute MemberWorkLogDetailOptions options
  ) {
    return adminManageLogFacade.getDetails(options);
  }

  @Operation(summary = "고객 작업이력 리스트")
  @GetMapping("/member-log/list")
  public ListResp<MemberWorkLogResp> memberLogList() {
    return adminManageLogFacade.getMemberList();
  }

  @Operation(summary = "매물 작업이력 상세내역")
  @GetMapping("/property-log")
  public PageResp<PropertyWorkLogDetailResp> getDetails(
      @ParameterObject @ModelAttribute PropertyWorkLogDetailOptions options
  ) {
    return adminManageLogFacade.getDetails(options);
  }

  @Operation(summary = "매물 작업이력 전체 변경 상세내역")
  @GetMapping("/property-log/detail")
  public PageResp<PropertyWorkLogDetailResp> getAllDetails(
      @ModelAttribute PropertyWorkLogDetailsOptions options
  ){
    return adminManageLogFacade.getAllDetails(options); //한 매물의 모든 변경로그 조회
  }

  @Operation(summary = "매물 작업이력 리스트")
  @GetMapping("/property-log/list")
  public ListResp<PropertyWorkLogResp> propertyLogList() {
    return adminManageLogFacade.getPropertyList();
  }

}