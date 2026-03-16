package com.backend.lab.api.admin.property.core.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;
import static com.backend.lab.common.util.NetworkUtil.getClientIp;


import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDetailResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;
import com.backend.lab.application.port.in.property.core.CreatePropertyUseCase;
import com.backend.lab.application.port.in.property.core.DeletePropertyUseCase;
import com.backend.lab.application.port.in.property.core.FloorUpdateUseCase;
import com.backend.lab.application.port.in.property.core.GetPropertyDetailUseCase;
import com.backend.lab.application.port.in.property.core.GetPropertyInfoUseCase;
import com.backend.lab.application.port.in.property.core.GetUpdatePropertyUseCase;
import com.backend.lab.application.port.in.property.core.UpdatePropertyUseCase;
import com.backend.lab.common.util.AuthUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 설정")
public class AdminPropertyController {

  private final CreatePropertyUseCase createPropertyUseCase;
  private final UpdatePropertyUseCase updatePropertyUseCase;
  private final GetPropertyDetailUseCase getPropertyDetailUseCase;
  private final GetUpdatePropertyUseCase getUpdatePropertyUseCase;
  private final GetPropertyInfoUseCase getPropertyInfoUseCase;
  private final DeletePropertyUseCase deletePropertyUseCase;
  private final FloorUpdateUseCase floorUpdateUseCase;

  @Hidden
  @Operation(summary = "층별정보 업데이트")
  @GetMapping("/floor-update")
  public void floorUpdate() {
    floorUpdateUseCase.floorUpdate();
  }

  //매물 생성
  @Operation(summary = "매물 생성")
  @PostMapping
  public void create(
      @RequestBody PropertyCreateReq req,
      HttpServletRequest request
  ) {
    createPropertyUseCase.execute(req, getUserId(), getClientIp(request));
  }

  @Operation(summary = "매물 수정 조회")
  @GetMapping("/{propertyId}")
  public PropertyUpdateResp update(
      @PathVariable("propertyId") Long propertyId
  ) {
    return getUpdatePropertyUseCase.getUpdateProperty(AuthUtil.getUserId(), propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 수정")
  @PutMapping("/{propertyId}")
  public void update(
      @RequestBody PropertyUpdateReq req,
      @PathVariable("propertyId") Long propertyId,
      HttpServletRequest request
  ) {
    updatePropertyUseCase.execute(req, getUserId(), propertyId, getClientIp(request));
  }

  @Operation(summary = "매물 개요")
  @GetMapping("/{propertyId}/info")
  public PropertyDetailResp getInfo(
      @PathVariable("propertyId") Long propertyId
  ) {
    return getPropertyInfoUseCase.getInfo(propertyId, getUserId());
  }

  @Operation(summary = "매물 상세 조회")
  @GetMapping("/detail/{propertyId}")
  public PropertyResp get(
      @PathVariable("propertyId") Long propertyId
  ) {
    return getPropertyDetailUseCase.get(propertyId, getUserId());
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 삭제")
  @DeleteMapping("/{propertyId}")
  public void delete(
      @PathVariable("propertyId") Long propertyId,
      HttpServletRequest request
  ) {
    deletePropertyUseCase.delete(propertyId, getUserId(), getClientIp(request));
  }

}
