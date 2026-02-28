//package com.backslash.kmorgan.api.admin.property.core.facade;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyCreateReq;
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyCustomerCreateReq;
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyDefaultReq;
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyFloorReq;
//import com.backslash.kmorgan.api.admin.property.core.dto.req.PropertyUpdateReq;
//import com.backslash.kmorgan.domain.admin.core.entity.Admin;
//import com.backslash.kmorgan.domain.admin.core.repository.AdminRepository;
//import com.backslash.kmorgan.domain.admin.core.service.AdminService;
//import com.backslash.kmorgan.domain.admin.organization.jobGrade.entity.JobGrade;
//import com.backslash.kmorgan.domain.admin.organization.jobGrade.repository.JobGradeRepository;
//import com.backslash.kmorgan.domain.admin.organization.jobGrade.service.JobGradeService;
//import com.backslash.kmorgan.domain.details.entity.dto.req.DetailReq;
//import com.backslash.kmorgan.domain.details.service.DetailsService;
//import com.backslash.kmorgan.domain.property.core.entity.Property;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.FloorProperties;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.LandProperties;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.LedgerProperties;
//import com.backslash.kmorgan.domain.property.core.entity.embedded.PriceProperties;
//import com.backslash.kmorgan.domain.property.core.entity.information.FloorInformation;
//import com.backslash.kmorgan.domain.property.core.entity.information.LandInformation;
//import com.backslash.kmorgan.domain.property.core.entity.information.LedgeInformation;
//import com.backslash.kmorgan.domain.property.core.entity.vo.PropertyStatus;
//import com.backslash.kmorgan.domain.property.core.repository.PropertyRepository;
//import com.backslash.kmorgan.domain.property.core.service.PropertyService;
//import com.backslash.kmorgan.domain.property.core.service.info.FloorInfoService;
//import com.backslash.kmorgan.domain.property.core.service.info.LandInfoService;
//import com.backslash.kmorgan.domain.property.core.service.info.LedgeInfoService;
//import com.backslash.kmorgan.domain.property.propertyWorkLog.entity.vo.LogFieldType;
//import com.backslash.kmorgan.domain.property.propertyWorkLog.entity.PropertyWorkLog;
//import com.backslash.kmorgan.domain.property.propertyWorkLog.entity.PropertyWorkLogDetail;
//import com.backslash.kmorgan.domain.property.propertyWorkLog.repository.PropertyWorkLogRepository;
//import com.backslash.kmorgan.domain.property.propertyWorkLog.service.PropertyChangeDetectService;
//import com.backslash.kmorgan.domain.property.taskNote.entity.dto.req.TaskNoteCreateReq;
//import com.backslash.kmorgan.domain.property.taskNote.entity.dto.req.TaskNoteReq;
//import com.backslash.kmorgan.domain.property.taskNote.entity.vo.TaskType;
//import com.backslash.kmorgan.domain.property.taskNote.entity.vo.WorkLogType;
//import com.backslash.kmorgan.domain.secret.entity.dto.req.SecretReq;
//import com.backslash.kmorgan.domain.secret.service.SecretService;
//import jakarta.persistence.EntityNotFoundException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//import java.util.stream.Collectors;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@Transactional
//@SpringBootTest
//public class UpdateTest {
//  @Autowired
//  private PropertyChangeDetectService propertyChangeDetectService;
//
//  @Autowired
//  private PropertyWorkLogRepository propertyWorkLogRepository;
//
//
//  @Autowired
//  private AdminPropertyFacade adminPropertyFacade;
//
//  @Autowired
//  private PropertyService propertyService;
//
//  @Autowired
//  private AdminService adminService;
//
//  @Autowired
//  private FloorInfoService floorInfoService;
//
//  @Autowired
//  private LandInfoService landInfoService;
//
//  @Autowired
//  private LedgeInfoService ledgeInfoService;
//
//  @Autowired
//  private SecretService secretService;
//
//  @Autowired
//  private DetailsService detailsService;
//
//  @Autowired
//  private AdminRepository adminRepository;
//
//  private Property testProperty;
//  private Admin testAdmin;
//  @Autowired
//  private JobGradeService jobGradeService;
//  @Autowired
//  private JobGradeRepository jobGradeRepository;
//  @Autowired
//  private PropertyRepository propertyRepository;
//
//
//  @BeforeEach
//  void setUp() {
//    JobGrade jobGrade = JobGrade.builder()
//        .name("대표")
//        .build();
//    JobGrade save = jobGradeRepository.save(jobGrade);
//    // 테스트용 Admin 생성
//    testAdmin = Admin.builder()
//        .name("테스트 관리자")
//        .email("test@test.com")
//        .jobGrade(save)
//        .build();
//    testAdmin = adminRepository.save(testAdmin);
//
//    // 테스트용 Property 생성 (기존 데이터 포함)
//    testProperty = createPropertyWithInitialData();
//  }
//
//  @Test
//  @Order(1)
//  @DisplayName("전체 정보 업데이트 테스트")
//  void updateProperty_WithAllData_Success() {
//    // Given
//    PropertyUpdateReq req = createCompleteUpdateRequest();
//
//    // When
//    adminPropertyFacade.updateProperty(req, testAdmin.getId(), testProperty.getId());
//
//    // Then
//    Property updatedProperty = propertyService.getById(testProperty.getId());
//
//    // 기본 정보 업데이트 확인
//    assertThat(updatedProperty.getBuildingName()).isEqualTo("업데이트된 건물명");
//    assertThat(updatedProperty.getIsPublic()).isEqualTo(req.getDefaults().isExclusive());
//    assertThat(updatedProperty.getStatus()).isEqualTo(req.getDefaults().getStatus());
//
//    // 토지 정보 업데이트 확인 (기존 삭제 후 새로 생성)
//    assertThat(updatedProperty.getLand()).hasSize(2);
//    assertThat(updatedProperty.getLand().get(0).getProperties().getAreaMeter()).isEqualTo(300.0);
//    assertThat(updatedProperty.getLand().get(1).getProperties().getAreaMeter()).isEqualTo(400.0);
//
//    // 건축물대장 정보 업데이트 확인
//    assertThat(updatedProperty.getLedge()).hasSize(2);
//    assertThat(updatedProperty.getLedge().get(0).getProperties().getMainPurpsCdNm()).isEqualTo("업데이트된 용도1");
//    assertThat(updatedProperty.getLedge().get(1).getProperties().getMainPurpsCdNm()).isEqualTo("업데이트된 용도2");
//
//    // 층 정보 업데이트 확인
//    assertThat(updatedProperty.getFloors()).hasSize(4); // 새로운 층 구성
//
//    // buildingOrder와 rank 재할당 확인
//    LinkedHashSet<FloorInformation> building1Floors = floorInfoService.getByBuildingOrder(1L);
//    LinkedHashSet<FloorInformation> building2Floors = floorInfoService.getByBuildingOrder(2L);
//
//    assertThat(building1Floors).hasSize(2);
//    assertThat(building2Floors).hasSize(2);
//
//    // rank 순서 확인
//    List<FloorInformation> building1List = new ArrayList<>(building1Floors);
//    assertThat(building1List.get(0).getRank()).isEqualTo(1L);
//    assertThat(building1List.get(1).getRank()).isEqualTo(2L);
//  }
//
//
//  @Test
//  @Order(2)
//  @DisplayName("부분 정보만 업데이트 테스트")
//  void updateProperty_WithPartialData_Success() {
//    // Given - floors와 defaults만 업데이트
//    PropertyUpdateReq req = PropertyUpdateReq.builder()
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(createFloorProperties("1층", 100, 30)))
//                .isPublic(true)
//                .build()
//        ))
//        .defaults(PropertyDefaultReq.builder()
//            .buildingName("부분업데이트 건물명")
//            .status(PropertyStatus.READY)
//            .build())
//        .build();
//
//    // When
//    adminPropertyFacade.updateProperty(req, testAdmin.getId(), testProperty.getId());
//
//    // Then
//    Property updatedProperty = propertyService.getById(testProperty.getId());
//
//    // defaults 업데이트 확인
//    assertThat(updatedProperty.getBuildingName()).isEqualTo("부분업데이트 건물명");
//
//    // floors만 업데이트되고 기존 land, ledge는 삭제됨
//    assertThat(updatedProperty.getFloors()).hasSize(1);
//    assertThat(updatedProperty.getLand()).isEmpty();
//    assertThat(updatedProperty.getLedge()).isEmpty();
//  }
//
//
//  @Test
//  @Order(3)
//  @DisplayName("기존 데이터 삭제 후 재생성 확인 테스트")
//  void updateProperty_DeleteAndRecreate_Success() {
//    // Given - 기존 데이터와 완전히 다른 구조
//    List<Long> originalFloorIds = testProperty.getFloors().stream()
//        .map(FloorInformation::getId)
//        .collect(Collectors.toList());
//    List<Long> originalLandIds = testProperty.getLand().stream()
//        .map(LandInformation::getId)
//        .collect(Collectors.toList());
//    List<Long> originalLedgeIds = testProperty.getLedge().stream()
//        .map(LedgeInformation::getId)
//        .collect(Collectors.toList());
//
//    PropertyUpdateReq req = PropertyUpdateReq.builder()
//        .lands(Arrays.asList(createLandProperties(500.0, "업데이트된 대지")))
//        .ledges(Arrays.asList(createLedgerProperties("완전히 새로운 용도", 20, 1)))
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("옥탑층", 50, 15),
//                    createFloorProperties("10층", 80, 24)
//                ))
//                .isPublic(false)
//                .build()
//        ))
//        .defaults(createDefaultRequest())
//        .build();
//
//    // When
//    adminPropertyFacade.updateProperty(req, testAdmin.getId(), testProperty.getId());
//
//    // Then
//    Property updatedProperty = propertyService.getById(testProperty.getId());
//
//    // 새로운 데이터 확인
//    assertThat(updatedProperty.getLand()).hasSize(1);
//    assertThat(updatedProperty.getLedge()).hasSize(1);
//    assertThat(updatedProperty.getFloors()).hasSize(2);
//
//    // 새로운 ID들이 할당되었는지 확인 (기존 데이터 삭제 확인)
//    List<Long> newFloorIds = updatedProperty.getFloors().stream()
//        .map(FloorInformation::getId)
//        .collect(Collectors.toList());
//    List<Long> newLandIds = updatedProperty.getLand().stream()
//        .map(LandInformation::getId)
//        .collect(Collectors.toList());
//    List<Long> newLedgeIds = updatedProperty.getLedge().stream()
//        .map(LedgeInformation::getId)
//        .collect(Collectors.toList());
//
//    // 기존 ID와 새로운 ID가 다른지 확인
//    assertThat(newFloorIds).doesNotContainAnyElementsOf(originalFloorIds);
//    assertThat(newLandIds).doesNotContainAnyElementsOf(originalLandIds);
//    assertThat(newLedgeIds).doesNotContainAnyElementsOf(originalLedgeIds);
//  }
//
//  @Test
//  @Order(4)
//  @DisplayName("null 값들로 업데이트 시 기존 데이터 삭제 테스트")
//  void updateProperty_WithNullValues_DeletesExistingData() {
//    // Given
//    PropertyUpdateReq req = PropertyUpdateReq.builder()
//        .lands(null)
//        .ledges(null)
//        .floors(null)
//        .defaults(createDefaultRequest())
//        .build();
//
//    // When
//    adminPropertyFacade.updateProperty(req, testAdmin.getId(), testProperty.getId());
//
//    // Then
//    Property updatedProperty = propertyService.getById(testProperty.getId());
//
//    // 모든 하위 정보가 삭제되었는지 확인
//    assertThat(updatedProperty.getLand()).isEmpty();
//    assertThat(updatedProperty.getLedge()).isEmpty();
//    assertThat(updatedProperty.getFloors()).isEmpty();
//  }
//
//  @Test
//  @Order(5)
//  @DisplayName("관련 데이터 업데이트 테스트 (비밀메모, 상세정보 등)")
//  void updateProperty_WithRelatedData_Success() {
//    // Given
//    PropertyUpdateReq req = PropertyUpdateReq.builder()
//        .secret(SecretReq.builder()
//            .content("업데이트된 비밀메모")
//            .build())
//        .detail(DetailReq.builder()
//            .content("업데이트된 상세정보")
//            .build())
//        .taskNote(TaskNoteReq.builder()
//            .taskNoteCreateReqs(Arrays.asList(
//                TaskNoteCreateReq.builder()
//                    .taskType(TaskType.CALL) // TaskType enum 값 사용
//                    .content("업데이트된 업무일지 내용1")
//                    .fileUrl("https://example.com/file1.pdf")
//                    .propertyId(testProperty.getId())
//                    .build(),
//                TaskNoteCreateReq.builder()
//                    .taskType(TaskType.ETC)
//                    .content("업데이트된 업무일지 내용2")
//                    .propertyId(testProperty.getId())
//                    .build()
//            ))
//            .build())
//        .defaults(createDefaultRequest())
//        .build();
//
//    // When
//    adminPropertyFacade.updateProperty(req, testAdmin.getId(), testProperty.getId());
//
//    // Then - 실제 생성/업데이트 여부는 해당 서비스의 로직에 따라 달라짐
//    // 여기서는 메서드 호출이 정상적으로 이루어지는지 확인
//    assertDoesNotThrow(() -> {
//      secretService.getByPropertyId(testProperty.getId());
//      detailsService.getById(testProperty.getId());
//    });
//  }
//
//  @Test
//  @Order(6)
//  @DisplayName("존재하지 않는 Property ID로 업데이트 시도시 예외 발생")
//  void updateProperty_NonExistentPropertyId_ThrowsException() {
//    // Given
//    PropertyUpdateReq req = createSimpleUpdateRequest();
//    Long nonExistentId = 999999L;
//
//    // When & Then
//    assertThatThrownBy(() ->
//        adminPropertyFacade.updateProperty(req, testAdmin.getId(), nonExistentId))
//        .isInstanceOf(EntityNotFoundException.class);
//  }
//
//  @Test
//  @Order(7)
//  @DisplayName("존재하지 않는 Admin ID로 업데이트 시도시 예외 발생")
//  void updateProperty_NonExistentAdminId_ThrowsException() {
//    // Given
//    PropertyUpdateReq req = createSimpleUpdateRequest();
//    Long nonExistentAdminId = 999999L;
//
//    // When & Then
//    assertThatThrownBy(() ->
//        adminPropertyFacade.updateProperty(req, nonExistentAdminId, testProperty.getId()))
//        .isInstanceOf(EntityNotFoundException.class);
//  }
//
//  @Test
//  @DisplayName("매물 생성시 WorkLog가 정상적으로 생성되는지 테스트")
//  void PropertyCreateWorkLog_Success() {
//    // Given
//    PropertyCreateReq req = createPropertyCreateRequest();
//    Property property = createTestProperty();
//
//    // When
//    propertyChangeDetectService.PropertyCreateWorkLog(req, property, testAdmin);
//
//    // Then
//    List<PropertyWorkLog> workLogs = propertyWorkLogRepository.findByPropertyOrderByCreatedAtDesc(property.getId());
//
//    assertThat(workLogs).hasSize(1);
//    PropertyWorkLog workLog = workLogs.get(0);
//    assertThat(workLog.getWorkLogType()).isEqualTo(WorkLogType.CREATED);
//
//    // 필수 필드들 검증 (5개)
//    assertThat(workLog.getDetails()).hasSizeGreaterThanOrEqualTo(5);
//
//    // 세부 검증
//    assertDetailExists(workLog.getDetails(), LogFieldType.ADMIN, null, testAdmin.getName() + " " + testAdmin.getJobGrade().getName());
//    assertDetailExists(workLog.getDetails(), LogFieldType.PHONE_NUMBER, null, "010-1234-5678 김철수, 010-9876-5432 이영희");
//    assertDetailExists(workLog.getDetails(), LogFieldType.MM_PRICE, null, "500000000");
//    assertDetailExists(workLog.getDetails(), LogFieldType.PYENG_PRICE, null, "1500000");
//    assertDetailExists(workLog.getDetails(), LogFieldType.STATUS, null, "READY");
//
//    // 선택 필드들 검증 (3개)
//    assertDetailExists(workLog.getDetails(), LogFieldType.ROI, null, "5.5");
//    assertDetailExists(workLog.getDetails(), LogFieldType.DEPOSIT_PRICE, null, "100000000");
//    assertDetailExists(workLog.getDetails(), LogFieldType.MONTH_PRICE, null, "3000000");
//  }
//
//  @Test
//  @DisplayName("매물 생성시 선택 필드가 null인 경우 테스트")
//  void PropertyCreateWorkLog_WithNullOptionalFields_Success() {
//    // Given
//    PropertyCreateReq req = createMinimalPropertyCreateRequest(); // ROI, 보증금, 월세 없음
//    Property property = createTestProperty();
//
//    // When
//    propertyChangeDetectService.PropertyCreateWorkLog(req, property, testAdmin);
//
//    // Then
//    List<PropertyWorkLog> workLogs = propertyWorkLogRepository.findByPropertyOrderByCreatedAtDesc(property.getId());
//
//    assertThat(workLogs).hasSize(1);
//    PropertyWorkLog workLog = workLogs.get(0);
//
//    // 필수 필드만 5개 있는지 확인
//    assertThat(workLog.getDetails()).hasSize(5);
//
//    // 선택 필드들이 없는지 확인
//    boolean hasROI = workLog.getDetails().stream()
//        .anyMatch(detail -> detail.getLogFieldType() == LogFieldType.ROI);
//    assertThat(hasROI).isFalse();
//  }
//
//  // Helper Methods 추가
//  private PropertyCreateReq createPropertyCreateRequest() {
//    // 가격 정보 (모든 필드 포함)
//    PriceProperties price = PriceProperties.builder()
//        .mmPrice(500000000)       // 필수
//        .pyeongPrice(1500000)     // 필수
//        .roi(5.5)                 // 선택
//        .depositPrice(100000000)  // 선택
//        .monthPrice(3000000)      // 선택
//        .build();
//
//    // 기본 정보
//    PropertyDefaultReq defaults = PropertyDefaultReq.builder()
//        .status(PropertyStatus.READY)  // 필수
//        .buildingName("테스트 매물")
//        .build();
//
//    // 고객 정보 (필수)
//    PropertyCustomerCreateReq member1 = PropertyCustomerCreateReq.builder()
//        .phoneNumber("010-1234-5678")
//        .name("김철수")
//        .build();
//
//    PropertyCustomerCreateReq member2 = PropertyCustomerCreateReq.builder()
//        .phoneNumber("010-9876-5432")
//        .name("이영희")
//        .build();
//
//    return PropertyCreateReq.builder()
//        .price(price)
//        .defaults(defaults)
//        .members(Arrays.asList(member1, member2))
//        .build();
//  }
//
//  private PropertyCreateReq createMinimalPropertyCreateRequest() {
//    // 가격 정보 (필수 필드만)
//    PriceProperties price = PriceProperties.builder()
//        .mmPrice(500000000)
//        .pyeongPrice(1500000)
//        // roi, depositPrice, monthPrice는 null
//        .build();
//
//    // 기본 정보
//    PropertyDefaultReq defaults = PropertyDefaultReq.builder()
//        .status(PropertyStatus.READY)
//        .build();
//
//    // 고객 정보
//    PropertyCustomerCreateReq member = PropertyCustomerCreateReq.builder()
//        .phoneNumber("010-1234-5678")
//        .name("김철수")
//        .build();
//
//    return PropertyCreateReq.builder()
//        .price(price)
//        .defaults(defaults)
//        .members(Arrays.asList(member))
//        .build();
//  }
//
//  private Property createTestProperty() {
//    return propertyRepository.save(Property.builder()
//        .buildingName("테스트 매물")
//        .status(PropertyStatus.READY)
//        .build());
//  }
//
//  private void assertDetailExists(List<PropertyWorkLogDetail> details,
//      LogFieldType expectedType,
//      String expectedBefore,
//      String expectedAfter) {
//    boolean found = details.stream()
//        .anyMatch(detail ->
//            detail.getLogFieldType() == expectedType &&
//                Objects.equals(detail.getBeforeValue(), expectedBefore) &&
//                Objects.equals(detail.getAfterValue(), expectedAfter)
//        );
//
//    assertThat(found)
//        .withFailMessage("Expected detail not found: type=%s, before=%s, after=%s",
//            expectedType, expectedBefore, expectedAfter)
//        .isTrue();
//  }
//
//  // Helper Methods
//  private Property createPropertyWithInitialData() {
//    // 초기 데이터가 포함된 Property 생성
//    Property property = Property.builder()
//        .buildingName("초기 건물명")
//        .isPublic(true)
//        .status(PropertyStatus.SOLD)
//        .build();
//
//    propertyService.create(property);
//
//    // 초기 토지, 건축물대장, 층 정보 추가
//    PropertyCreateReq initialReq = PropertyCreateReq.builder()
//        .lands(Arrays.asList(
//            createLandProperties(100.0, "초기대지1"),
//            createLandProperties(200.0, "초기대지2")
//        ))
//        .ledges(Arrays.asList(
//            createLedgerProperties("초기용도1", 5, 1),
//            createLedgerProperties("초기용도2", 10, 1)
//        ))
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("1층", 50, 15),
//                    createFloorProperties("2층", 50, 15)
//                ))
//                .isPublic(true)
//                .build(),
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("지1층", 40, 12)
//                ))
//                .isPublic(false)
//                .build()
//        ))
//        .build();
//
//    adminPropertyFacade.createInfoList(initialReq.getLands(),initialReq.getLedges(),initialReq.getFloors(), property.getId());
//
//    return propertyService.getById(property.getId());
//  }
//
//  private PropertyUpdateReq createCompleteUpdateRequest() {
//    return PropertyUpdateReq.builder()
//        .lands(Arrays.asList(
//            createLandProperties(300.0, "업데이트된 대지1"),
//            createLandProperties(400.0, "업데이트된 대지2")
//        ))
//        .ledges(Arrays.asList(
//            createLedgerProperties("업데이트된 용도1", 8, 1),
//            createLedgerProperties("업데이트된 용도2", 15, 1)
//        ))
//        .floors(Arrays.asList(
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("3층", 70, 21),
//                    createFloorProperties("4층", 70, 21)
//                ))
//                .isPublic(true)
//                .build(),
//            PropertyFloorReq.builder()
//                .floor(Set.of(
//                    createFloorProperties("지2층", 60, 18),
//                    createFloorProperties("지3층", 60, 18)
//                ))
//                .isPublic(false)
//                .build()
//        ))
//        .defaults(PropertyDefaultReq.builder()
//            .buildingName("업데이트된 건물명")
//            .status(PropertyStatus.READY)
//            .exclusive(false)
//            .build())
//        .addressId(1L)
//        .priceId(1L)
//        .registerId(1L)
//        .templateId(1L)
//        .build();
//  }
//
//
//
//  private PropertyUpdateReq createSimpleUpdateRequest() {
//    return PropertyUpdateReq.builder()
//        .defaults(createDefaultRequest())
//        .build();
//  }
//
//  private PropertyDefaultReq createDefaultRequest() {
//    return PropertyDefaultReq.builder()
//        .buildingName("테스트 건물명")
//        .status(PropertyStatus.READY)
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
//}
