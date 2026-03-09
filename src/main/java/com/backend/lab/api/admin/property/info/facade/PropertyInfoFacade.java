package com.backend.lab.api.admin.property.info.facade;

import com.backend.lab.api.admin.property.info.dto.BuildingInfo;
import com.backend.lab.api.admin.property.info.dto.PropertySelectedBuildingInfoReq;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyBuildingInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLandInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLedgerInfoResp;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyMoveInfoResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.openapi.dto.landPossession.LandPossessionResp;
import com.backend.lab.common.openapi.service.gong.FloorApiPort;
import com.backend.lab.common.openapi.service.toji.LandCharacteristicApiPort;
import com.backend.lab.common.openapi.service.toji.LandPossessionApiPort;
import com.backend.lab.common.openapi.service.toji.LandUsePlanApiPort;
import com.backend.lab.common.openapi.service.tojiList.LandMoveApiPort;
import com.backend.lab.common.openapi.service.gong.LedgerApiPort;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.common.openapi.dto.landCharacteristic.LandCharacteristicResp;
import com.backend.lab.common.openapi.dto.landUsePlan.LandUseResp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyInfoFacade {

  private final LedgerApiPort ledgerApiPort;
  private final FloorApiPort floorApiPort;

  private final LandMoveApiPort landMoveApiPort;
  private final LandCharacteristicApiPort landCharacteristicApiPort;
  private final LandPossessionApiPort landPossessionApiPort;
  private final LandUsePlanApiPort landUsePlanApiPort;


  //토지정보 조회
  public PropertyLandInfoResp getLandInfo(String pnu) {

    try {
      // 3개 API를 병렬로 호출
      CompletableFuture<LandCharacteristicResp> characteristicsFuture =
          landCharacteristicApiPort.getLandCharacteristicInfo(pnu);

      CompletableFuture<LandPossessionResp> possessionFuture =
          landPossessionApiPort.getLandOwnerInfo(pnu);

      CompletableFuture<LandUseResp> landUsesFuture =
          landUsePlanApiPort.getLandUsePlanInfo(pnu);

      // 모든 결과 대기
      CompletableFuture<Void> allFutures = CompletableFuture.allOf(
          characteristicsFuture, possessionFuture, landUsesFuture);

      allFutures.get(20, TimeUnit.SECONDS);

      // 결과 수집
      LandCharacteristicResp characteristics = getResultSafely(characteristicsFuture);
      LandPossessionResp possession = getResultSafely(possessionFuture);
      LandUseResp landUse = getResultSafely(landUsesFuture);

      LandProperties landProperties = mapToLandProperties(characteristics, landUse, possession);
      landProperties.setPnu(pnu);

      return PropertyLandInfoResp
          .builder()
          .land(landProperties)
          .build();

    } catch (InterruptedException | ExecutionException |TimeoutException e) {
      String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new BusinessException(ErrorCode.valueOf(errorMessage), e);
    }
  }

  private <T> T getResultSafely(CompletableFuture<T> future) {
    try {
      return future.get();
    } catch (Exception e) {
      return null;
    }
  }

  //건물 목록 조회
  public PropertyBuildingInfoResp getBuildingInfo(String pnu) {
    try {
      CompletableFuture<List<BuildingInfo>> buildingInfoFuture = ledgerApiPort.getBuildingInfo(pnu);
      List<BuildingInfo> buildingInfoList = buildingInfoFuture.get();

      return PropertyBuildingInfoResp.builder()
          .pnu(pnu)
          .buildings(buildingInfoList)
          .totalCount(buildingInfoList.size())
          .build();
    } catch (InterruptedException | ExecutionException e) {
      String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new BusinessException(ErrorCode.valueOf(errorMessage), e);
    }
  }

  //건축물대장 조회
  public PropertyLedgerInfoResp getLedgerInfo(PropertySelectedBuildingInfoReq req) {
    List<LedgerProperties> ledgerProperties = new ArrayList<>();
    try {
      CompletableFuture<List<LedgerProperties>> ledgerInfo = ledgerApiPort.getLedgerInfo(
          req.getPnu(), req.getBuildings());
      ledgerProperties = ledgerInfo.get();
    } catch (InterruptedException | ExecutionException e) {
      String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new BusinessException(ErrorCode.valueOf(errorMessage), e);
    }
    return PropertyLedgerInfoResp.builder()
        .ledge(ledgerProperties)
        .build();
  }

  //토지이동정보 조회
  public PropertyMoveInfoResp getMoves(String pnu) {
    List<PropertyMoveInfoItem> propertyMoveInfoItems = null;
    try {
      CompletableFuture<List<PropertyMoveInfoItem>> landMovesInfo = landMoveApiPort.getLandMovesInfo(
          pnu);
      propertyMoveInfoItems = landMovesInfo.get();
    } catch (InterruptedException | ExecutionException e) {
      String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new BusinessException(ErrorCode.valueOf(errorMessage), e);
    }
    List<PropertyMoveInfoItem> list = propertyMoveInfoItems.stream().map(
        moveInfoItem -> PropertyMoveInfoItem.builder()
            .moveReason(moveInfoItem.getMoveReason())
            .moveDate(moveInfoItem.getMoveDate())
            .build()
    ).toList();

    return PropertyMoveInfoResp.builder()
        .data(list)
        .build();
  }

  //층 정보 조회
  public PropertyFloorInfoResp getFloors(String pnu) {
    try {
      CompletableFuture<List<PropertyFloorInfoItem>> floorInfo = floorApiPort.getFloorInfo(pnu);
      List<PropertyFloorInfoItem> floors = floorInfo.get();

      return PropertyFloorInfoResp.builder()
          .data(floors)
          .build();
    } catch (InterruptedException | ExecutionException e) {
      String errorMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new BusinessException(ErrorCode.valueOf(errorMessage), e);
    }

  }



  private LandProperties toLandProps(LandCharacteristicResp c) {
    if (c == null) return null;

    LandProperties lp = new LandProperties();
    // 면적·지목·공시지가
    lp.setAreaMeter(c.getLandArea());                     // 토지면적(㎡)
    lp.setDongJibun(c.getDongJibun());
    lp.setAreaPyung(c.getPyengLandArea()); //평
    lp.setGonsiPricePerMeter(c.getOfficialLandPrice()); //공시지가 미터당
    lp.setGonsiPricePerPyung(c.getPyengLandPrice()); //공시지가 평당
    lp.setTotalGonsiPrice(c.getTotalLandPrice());
    lp.setJimok(c.getLandCategoryName());              // 지목
    lp.setYongdo(c.getLandUseZoneName1()); //용도지역
    lp.setIyongSituation(c.getLandUseStatus()); //이용상황

    lp.setDoroJeopMyun(c.getRoadContact()); //도로접면
    lp.setLandHeight(c.getTerrainHeight()); //지형높이
    lp.setLandShape(c.getTerrainShape()); //지형형상

    return lp;
  }

  private LandProperties toLandProps(LandUseResp u) {
    if (u == null) return null;

    LandProperties lp = new LandProperties();

    // Helper lambda: 필터링 후 이름만 추출해 "," 로 연결
    java.util.function.Function<String, String> joinNames =
        status -> u.getNationalLawItems() == null ? null :
            u.getNationalLawItems().stream()
                .filter(i -> status.equals(i.getConstructableStatus()))
                .map(LandUseResp.Item::getName)
                .collect(Collectors.joining(","));

    java.util.function.Function<String, String> joinNamesEtc =
        status -> u.getOtherLawItems() == null ? null :
            u.getOtherLawItems().stream()
                .filter(i -> status.equals(i.getConstructableStatus()))
                .map(LandUseResp.Item::getName)
                .collect(Collectors.joining(","));

    // ── 국계법 ─────────────────────────────────────
    lp.setPhUsagePlanWithLaw(joinNames.apply("포함"));
    lp.setJcUsagePlanWithLaw(joinNames.apply("저촉"));
    lp.setJhUsagePlanWithLaw(joinNames.apply("접함"));

    // ── 기타법률 ──────────────────────────────────
    lp.setPhUsagePlanWithEtc(joinNamesEtc.apply("포함"));
    lp.setJcUsagePlanWithEtc(joinNamesEtc.apply("저촉"));
    lp.setJhUsagePlanWithEtc(joinNamesEtc.apply("접함"));

    return lp;
  }

  private LandProperties toLandProps(LandPossessionResp p) {
    if (p == null) return null;
    LandProperties lp = new LandProperties();
    lp.setOwnerType(p.getPosesnSeCodeNm());
    lp.setOwnerChangeDate(LocalDate.parse(p.getOwnshipChgDe()));
    lp.setOwnerChangeReason(p.getOwnshipChgCauseCodeNm());
    return lp;
  }


  private LandProperties mapToLandProperties(LandCharacteristicResp c,
      LandUseResp u, LandPossessionResp o) {
    LandProperties base = new LandProperties();
    return base
        .update(toLandProps(c))
        .update(toLandProps(u))
        .update(toLandProps(o));
  }
}
