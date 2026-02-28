//package com.backslash.kmorgan.api.admin.property.core.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyCreateReq;
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyFloorReq;
//import com.backslash.kmorgan.api.admin.property.core.facade.AdminPropertyFacade;
//import com.backslash.kmorgan.api.admin.property.core.facade.FloorFacade;
//import com.backslash.kmorgan.domain.property.core.entity.Property;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.FloorProperties;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.LandProperties;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.LedgerProperties;
//import com.backslash.kmorgan.domain.property.core.entity.information.FloorInformation;
//import com.backslash.kmorgan.domain.property.core.service.PropertyService;
//import com.backslash.kmorgan.domain.property.core.service.info.FloorInfoService;
//import com.backslash.kmorgan.domain.property.core.service.info.LandInfoService;
//import com.backslash.kmorgan.domain.property.core.service.info.LedgeInfoService;
//import jakarta.persistence.EntityNotFoundException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//class AdminPropertyControllerTest {
//
//  @Autowired
//  private FloorFacade floorFacade;
//
//  @Autowired
//  private AdminPropertyFacade adminPropertyFacade;
//
//  @Autowired
//  private PropertyService propertyService;
//
//  @Autowired
//  private FloorInfoService floorInfoService;
//
//  @Autowired
//  private LandInfoService landInfoService;
//
//
//
//  @Autowired
//  private LedgeInfoService ledgeInfoService;
//
//  private Property testProperty;
//
//  @BeforeEach
//  void setUp() {
//    // 테스트용 Property 생성
//    testProperty = Property.builder()
//        .buildingName("테스트네임")
//        .build();
//    propertyService.create(testProperty);
//  }
//
//  @Test
//  @Order(1)
//  @DisplayName("정상적인 모든 정보가 포함된 요청 처리 테스트")
//  @Transactional
//  void createInfoList_WithAllData_Success() {
//    // Given
//    PropertyCreateReq req = createCompleteRequest();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    // 토지 정보 검증
//    assertThat(savedProperty.getLand()).hasSize(2);
//    assertThat(savedProperty.getLand().get(0).getProperties().getAreaMeter()).isEqualTo(100.0);
//    assertThat(savedProperty.getLand().get(1).getProperties().getAreaMeter()).isEqualTo(200.0);
//
//    // 건축물대장 정보 검증
//    assertThat(savedProperty.getLedge()).hasSize(2);
//    assertThat(savedProperty.getLedge().get(0).getBuildingOrder()).isEqualTo(1L);
//    assertThat(savedProperty.getLedge().get(1).getBuildingOrder()).isEqualTo(2L);
//    assertThat(savedProperty.getLedge().get(0).getProperties().getMainPurpsCdNm()).isEqualTo("주거용");
//
//    // 층 정보 검증
//    assertThat(savedProperty.getFloors()).hasSize(6); // 1층,2층,3층 + 지1층,지2층,지3층
//
//    // 층 정보 순서 검증 (getFloorPriority 로직에 따라)
//    List<FloorInformation> sortedFloors = savedProperty.getFloors().stream()
//        .sorted(Comparator.comparing(FloorInformation::getRank))
//        .toList();
//
//
//    LinkedHashSet<FloorInformation> building1Floors = floorInfoService.getByBuildingOrder(1L);
//    LinkedHashSet<FloorInformation> building2Floors = floorInfoService.getByBuildingOrder(2L);
//
//    // 1번 건물 층 정보 검증
//    assertThat(building1Floors).hasSize(3);
//    List<FloorInformation> building1List = new ArrayList<>(building1Floors);
//    assertThat(building1List.get(0).getProperties().getFloor()).isEqualTo("3층");
//    assertThat(building1List.get(0).getRank()).isEqualTo(1L);
//    assertThat(building1List.get(1).getProperties().getFloor()).isEqualTo("2층");
//    assertThat(building1List.get(1).getRank()).isEqualTo(2L);
//    assertThat(building1List.get(2).getProperties().getFloor()).isEqualTo("1층");
//    assertThat(building1List.get(2).getRank()).isEqualTo(3L);
//
//    // 2번 건물 층 정보 검증
//    assertThat(building2Floors).hasSize(3);
//    List<FloorInformation> building2List = new ArrayList<>(building2Floors);
//    assertThat(building2List.get(0).getProperties().getFloor()).isEqualTo("지1층");
//    assertThat(building2List.get(0).getRank()).isEqualTo(1L);
//    assertThat(building2List.get(1).getProperties().getFloor()).isEqualTo("지2층");
//    assertThat(building2List.get(1).getRank()).isEqualTo(2L);
//    assertThat(building2List.get(2).getProperties().getFloor()).isEqualTo("지3층");
//    assertThat(building2List.get(2).getRank()).isEqualTo(3L);
//
//    // buildingOrder 검증
//    building1Floors.forEach(floor ->
//        assertThat(floor.getBuildingOrder()).isEqualTo(1L));
//    building2Floors.forEach(floor ->
//        assertThat(floor.getBuildingOrder()).isEqualTo(2L));
//  }
//
//  @Test
//  @Order(2)
//  @DisplayName("층 정보만 있는 요청 처리 테스트")
//  void createInfoList_OnlyFloors_Success() {
//    // Given
//    PropertyCreateReq req = PropertyCreateReq.builder()
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("1층", 50, 15),
//                    createFloorProperties("2층", 50, 15)
//                ))
//                .isPublic(true)
//                .build()
//        ))
//        .build();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    assertThat(savedProperty.getLand()).isEmpty();
//    assertThat(savedProperty.getLedge()).isEmpty();
//    assertThat(savedProperty.getFloors()).hasSize(2);
//
//    savedProperty.getFloors().forEach(floor -> {
//      assertThat(floor.getIsPublic()).isTrue();
//      assertThat(floor.getBuildingOrder()).isEqualTo(1L);
//    });
//  }
//
//  @Test
//  @Order(3)
//  @DisplayName("빈 리스트 요청 처리 테스트")
//  void createInfoList_EmptyLists_DoesNothing() {
//    // Given
//    PropertyCreateReq req = PropertyCreateReq.builder()
//        .lands(new ArrayList<>())
//        .ledges(new ArrayList<>())
//        .floors(new ArrayList<>())
//        .build();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    assertThat(savedProperty.getLand()).isEmpty();
//    assertThat(savedProperty.getLedge()).isEmpty();
//    assertThat(savedProperty.getFloors()).isEmpty();
//  }
//
//  @Test
//  @Order(4)
//  @DisplayName("null 리스트 요청 처리 테스트")
//  void createInfoList_NullLists_DoesNothing() {
//    // Given
//    PropertyCreateReq req = PropertyCreateReq.builder()
//        .lands(null)
//        .ledges(null)
//        .floors(null)
//        .build();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    assertThat(savedProperty.getLand()).isEmpty();
//    assertThat(savedProperty.getLedge()).isEmpty();
//    assertThat(savedProperty.getFloors()).isEmpty();
//  }
//
//  @Test
//  @Order(5)
//  @DisplayName("다른 크기의 리스트 처리 테스트")
//  void createInfoList_DifferentSizeLists_ProcessesCorrectly() {
//    // Given - 토지 1개, 건축물대장 3개, 층 2개
//    PropertyCreateReq req = PropertyCreateReq.builder()
//        .lands(Arrays.asList(
//            createLandProperties(150.0, "대지")
//        ))
//        .ledges(Arrays.asList(
//            createLedgerProperties("상업용", 10, 1),
//            createLedgerProperties("주거용", 5, 1),
//            createLedgerProperties("업무용", 15, 1)
//        ))
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(createFloorProperties("1층", 100, 30)))
//                .isPublic(true)
//                .build(),
//            PropertyFloorReq.builder()
//                .floor(Set.of(createFloorProperties("2층", 100, 30)))
//                .isPublic(false)
//                .build()
//        ))
//        .build();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    assertThat(savedProperty.getLand()).hasSize(1);
//    assertThat(savedProperty.getLedge()).hasSize(3);
//    assertThat(savedProperty.getFloors()).hasSize(2);
//
//    // buildingOrder 검증
//    assertThat(savedProperty.getLedge().get(0).getBuildingOrder()).isEqualTo(1L);
//    assertThat(savedProperty.getLedge().get(1).getBuildingOrder()).isEqualTo(2L);
//    assertThat(savedProperty.getLedge().get(2).getBuildingOrder()).isEqualTo(3L);
//  }
//
//  @Test
//  @Order(6)
//  @DisplayName("층 정보 우선순위 정렬 테스트")
//  void createInfoList_FloorPrioritySorting_WorksCorrectly() {
//    // Given
//    PropertyCreateReq req = PropertyCreateReq.builder()
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("지3층", 50, 15),
//                    createFloorProperties("1층", 60, 18),
//                    createFloorProperties("옥탑2층", 30, 9),
//                    createFloorProperties("지1층", 55, 16),
//                    createFloorProperties("5층", 50, 15),
//                    createFloorProperties("옥탑1층", 25, 7)
//                ))
//                .isPublic(true)
//                .build()
//        ))
//        .build();
//
//    // When
//    adminPropertyFacade.createInfoList(req.getLands(),req.getLedges(),req.getFloors(), testProperty.getId());
//    // Then
//    Property savedProperty = propertyService.getById(testProperty.getId());
//
//    List<FloorInformation> sortedFloors = savedProperty.getFloors().stream()
//        .sorted(Comparator.comparing(FloorInformation::getRank))
//        .collect(Collectors.toList());
//
//    // 정렬 순서: 옥탑2층, 옥탑1층, 5층, 1층, 지1층, 지3층
//    assertThat(sortedFloors.get(0).getProperties().getFloor()).isEqualTo("옥탑2층");
//    assertThat(sortedFloors.get(1).getProperties().getFloor()).isEqualTo("옥탑1층");
//    assertThat(sortedFloors.get(2).getProperties().getFloor()).isEqualTo("5층");
//    assertThat(sortedFloors.get(3).getProperties().getFloor()).isEqualTo("1층");
//    assertThat(sortedFloors.get(4).getProperties().getFloor()).isEqualTo("지1층");
//    assertThat(sortedFloors.get(5).getProperties().getFloor()).isEqualTo("지3층");
//
//    // rank 값 연속성 확인
//    for (int i = 0; i < sortedFloors.size(); i++) {
//      assertThat(sortedFloors.get(i).getRank()).isEqualTo(i + 1);
//    }
//  }
//
//  @Test
//  @Order(7)
//  @DisplayName("존재하지 않는 Property ID로 요청시 예외 발생")
//  void createInfoList_NonExistentPropertyId_ThrowsException() {
//    // Given
//    PropertyCreateReq req = createSimpleRequest();
//    Long nonExistentId = 999999L;
//
//
//  }
//
//  // Helper Methods
//  private PropertyCreateReq createCompleteRequest() {
//    return PropertyCreateReq.builder()
//        .lands(Arrays.asList(
//            createLandProperties(100.0, "대지"),
//            createLandProperties(200.0, "임야")
//        ))
//        .ledges(Arrays.asList(
//            createLedgerProperties("주거용", 5, 1),
//            createLedgerProperties("상업용", 10, 1)
//        ))
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("1층", 80, 24),
//                    createFloorProperties("2층", 80, 24),
//                    createFloorProperties("3층", 80, 24)
//                ))
//                .isPublic(true)
//                .build(),
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("지1층", 60, 18),
//                    createFloorProperties("지2층", 60, 18),
//                    createFloorProperties("지3층", 60, 18)
//                ))
//                .isPublic(false)
//                .build()
//        ))
//        .build();
//  }
//
//  private PropertyCreateReq createSimpleRequest() {
//    return PropertyCreateReq.builder()
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(createFloorProperties("1층", 50, 15)))
//                .isPublic(true)
//                .build()
//        ))
//        .build();
//  }
//
//  private LandProperties createLandProperties(Double areaMeter, String jimok) {
//    return LandProperties.builder()
//        .areaMeter(areaMeter)
//        .areaPyung(areaMeter * 0.3025)
//        .jimok(jimok)
//        .yongdo("주거지역")
//        .gonsiPricePerMeter(1000000)
//        .build();
//  }
//
//  private LedgerProperties createLedgerProperties(String mainPurpose, Integer maxFloor, Integer minFloor) {
//    return LedgerProperties.builder()
//        .mainPurpsCdNm(mainPurpose)
//        .maxFloor(maxFloor)
//        .minFloor(minFloor)
//        .structure("철근콘크리트구조")
//        .landAreaMeter(300.0)
//        .landAreaPyeong(90.75)
//        .buildingAreaMeter(200.0)
//        .buildingAreaPyeong(60.5)
//        .geonPye(60.0)
//        .yongjeok(200.0)
//        .build();
//  }
//
//  private FloorProperties createFloorProperties(String floor, Integer areaMeter, Integer areaPyung) {
//    return FloorProperties.builder()
//        .floor(floor)
//        .areaMeter(areaMeter)
//        .areaPyung(areaPyung)
//        .upjong("일반업무시설")
//        .depositPrice(10000000)
//        .monthlyPrice(1000000)
//        .grPrice(100000)
//        .isEmpty(false)
//        .isHidden(false)
//        .build();
//  }
//
//}