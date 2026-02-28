package com.backend.lab.api.admin.dbUpdate.controller;

import com.backend.lab.api.admin.dbUpdate.facade.AdminConflictFacade;
import com.backend.lab.api.admin.dbUpdate.facade.AdminPnuFacade;
import com.backend.lab.api.admin.dbUpdate.facade.dto.ConflictResolveReq;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.pnuTable.entity.PnuTableHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequireAdminRole
@RestController
@RequestMapping("/admin/db-update")
@RequiredArgsConstructor
@Tag(name = "[관리자] DB 업데이트")
public class AdminDbUpdateController {

  private final AdminPnuFacade pnuFacade;
  private final AdminConflictFacade conflictFacade;

  @Operation(summary = "pnu 업데이트 파일 업로드")
  @PostMapping(path = "/pnu", consumes = "multipart/form-data", produces = "application/json")
  public void process(@RequestParam("file") MultipartFile file) {
    pnuFacade.process(file);
  }

  @Operation(summary = "충돌 매물 조회")
  @GetMapping("/conflict")
  public PageResp<PropertySearchResp> conflicts(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return conflictFacade.gets(options);
  }

  @Operation(summary = "충돌 매물 해결(주소 새로 등록)")
  @PostMapping("/conflict/resolve/{propertyId}")
  public void resolve(
      @PathVariable("propertyId") Long propertyId,
      @RequestBody ConflictResolveReq req
  ) {
    conflictFacade.resolve(propertyId, req);
  }

  @Operation(summary = "기록 조회")
  @GetMapping("/pnu/history")
  public PageResp<PnuTableHistory> getsHistory(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return pnuFacade.getsHistory(options);
  }
}
