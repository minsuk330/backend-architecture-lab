package com.backend.lab.api.admin.property.info.controller;

import com.backend.lab.api.admin.property.info.dto.PropertySelectedBuildingInfoReq;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyBuildingInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLandInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLedgerInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoResp;
import com.backend.lab.api.admin.property.info.facade.PropertyInfoFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/property/info")
@RequiredArgsConstructor
@Tag(name = "[관리자] 토지 정보 + 건축물대장 정보 불러오기")
@Slf4j
public class PropertyInfoController {

  private final PropertyInfoFacade propertyInfoFacade;

  @Operation(summary = "토지 정보 조회")
  @GetMapping("/land/{pnu}")
  public PropertyLandInfoResp getLandInfo(
      @PathVariable("pnu") String pnu
  ) {
    return propertyInfoFacade.getLandInfo(pnu);
  }

  @Operation(summary = "건물 목록 조회")
  @GetMapping("/ledge/{pnu}")
  public PropertyBuildingInfoResp getBuildingInfo(
      @PathVariable("pnu") String pnu
  ) {
    return propertyInfoFacade.getBuildingInfo(pnu);
  }

  @Operation(summary = "건축물 대장 반환")
  @PostMapping("/select-ledge")
  public PropertyLedgerInfoResp getLedgerInfo(
      @RequestBody PropertySelectedBuildingInfoReq req
  ) {
    return propertyInfoFacade.getLedgerInfo(req);
  }

  // 층별 정보 반환
  @Operation(summary = "층별 정보 반환")
  @GetMapping("/floor/{pnu}")
  public PropertyFloorInfoResp getPropertyFloorInfo(
      @PathVariable("pnu") String pnu
  ) {
    return propertyInfoFacade.getFloors(pnu);
  }

  //토지이동사유
  @Operation(summary = "토지 이동 사유")
  @GetMapping("/move/{pnu}")
  public PropertyMoveInfoResp getPropertyMoveInfo(
      @PathVariable("pnu") String pnu
  ) {
    return propertyInfoFacade.getMoves(pnu);
  }

}
