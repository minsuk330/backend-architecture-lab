package com.backend.lab.api.admin.property.core.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.property.core.dto.req.PropertyStatusUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.application.port.in.property.status.ConfirmPropertyUseCase;
import com.backend.lab.application.port.in.property.status.GetPropertyStatusUseCase;
import com.backend.lab.application.port.in.property.status.RemoveConfirmUseCase;
import com.backend.lab.application.port.in.property.status.TogglePublicUseCase;
import com.backend.lab.application.port.in.property.status.UpdatePropertyStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 상태변경")
public class AdminPropertyStatusController {

  private final TogglePublicUseCase togglePublicUseCase;
  private final GetPropertyStatusUseCase getPropertyStatusUseCase;
  private final UpdatePropertyStatusUseCase updatePropertyStatusUseCase;
  private final ConfirmPropertyUseCase confirmPropertyUseCase;
  private final RemoveConfirmUseCase removeConfirmUseCase;

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 공개여부 토글")
  @PatchMapping("/{propertyId}/public")
  public PropertyPublicResp isPublicToggle(
      @PathVariable("propertyId") Long propertyId
  ) {
    return togglePublicUseCase.toggle(propertyId);
  }

  @Operation(summary = "매물 상태조회")
  @GetMapping("{propertyId}/status")
  public PropertyStatusResp getPropertyStatus(
      @PathVariable("propertyId") Long propertyId
  ) {
    return getPropertyStatusUseCase.getStatus(propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 상태변경")
  @PutMapping("{propertyId}/status")
  public PropertyStatusResp updatePropertyStatus(
      @PathVariable("propertyId") Long propertyId,
      @RequestBody PropertyStatusUpdateReq req
  ) {
    return updatePropertyStatusUseCase.update(propertyId, req, getUserId());
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 확인일 설정/갱신")
  @PatchMapping("/{propertyId}/confirm")
  public void confirm(
      @PathVariable("propertyId") Long propertyId
  ) {
    confirmPropertyUseCase.confirm(propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 확인일 삭제")
  @DeleteMapping("/{propertyId}/confirm")
  public void removeConfirm(
      @PathVariable("propertyId") Long propertyId
  ) {
    removeConfirmUseCase.removeConfirm(propertyId);
  }
}
