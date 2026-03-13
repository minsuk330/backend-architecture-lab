package com.backend.lab.application.service.property.file;

import com.backend.lab.domain.member.core.service.CustomerService.MemberInfo;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLandInfoResp;
import com.backend.lab.api.admin.property.info.facade.PropertyInfoFacade;
import com.backend.lab.application.port.in.property.file.UploadExcelUseCase;
import com.backend.lab.common.openapi.service.gong.FloorApiPort;
import com.backend.lab.common.openapi.service.kakao.KakaoApiService;
import com.backend.lab.common.openapi.service.kakao.dto.KakaoAddressSearchResponse;
import com.backend.lab.common.openapi.service.kakao.dto.KakaoAddressSearchResponse.KakaoDocument;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.repository.DetailsRepository;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.service.CategoryService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.embedded.FloorProperties;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.core.entity.embedded.TemplateProperties;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.entity.information.PriceInformation;
import com.backend.lab.domain.property.core.entity.information.RegisterInformation;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.repository.PropertyRepository;
import com.backend.lab.domain.property.core.repository.info.AddressInfoRepository;
import com.backend.lab.domain.property.core.repository.info.FloorInfoRepository;
import com.backend.lab.domain.property.core.repository.info.LandInfoRepository;
import com.backend.lab.domain.property.core.repository.info.LedgeInfoRepository;
import com.backend.lab.domain.property.core.repository.info.PriceInfoRepository;
import com.backend.lab.domain.property.core.repository.info.RegisterInfoRepository;
import com.backend.lab.domain.property.core.repository.info.TemplateInfoRepository;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadExcelService implements UploadExcelUseCase {

    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final AdminService adminService;
    private final CategoryService categoryService;
    private final KakaoApiService kakaoApiService;
    private final FloorApiPort floorApiPort;
    private final PropertyInfoFacade propertyInfoFacade;
    private final AddressInfoRepository addressInfoRepository;
    private final PriceInfoRepository priceInfoRepository;
    private final RegisterInfoRepository registerInfoRepository;
    private final TemplateInfoRepository templateInfoRepository;
    private final LedgeInfoRepository ledgeInfoRepository;
    private final LandInfoRepository landInfoRepository;
    private final FloorInfoRepository floorInfoRepository;
    private final DetailsRepository detailsRepository;
    private final PropertyMemberService propertyMemberService;
    private final CustomerService customerService;

    @Override
    @Transactional
    public void uploadExcelFile(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                try {
                    Property property = parseRowToProperty(row);
                    if (property != null) {
                        log.info("Property 생성 완료 - 매물번호: {}", property.getId());
                    }
                } catch (Exception e) {
                    log.error("행 {} 처리 실패: {}", row.getRowNum(), e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("엑셀 업로드 실패", e);
            throw new RuntimeException("업로드 실패: " + e.getMessage());
        }
    }

    private Property parseRowToProperty(Row row) {
        try {
            String propertyIdStr = getCellValue(row, 0);
            String isPublicStr = getCellValue(row, 2);
            String statusStr = getCellValue(row, 3);
            String buildingName = getCellValue(row, 5);
            String bigCategoryName = getCellValue(row, 6);
            String smallCategoryName = getCellValue(row, 7);

            LocalDateTime createdAt = parseDateTime(getCellValue(row, 11));
            LocalDateTime updatedAt = parseDateTime(getCellValue(row, 12));

            if (buildingName.equals("-")) {
                buildingName = "";
            }

            if (propertyIdStr.isEmpty()) {
                log.warn("필수 정보 누락으로 행 건너뜀 - 매물번호: {}", propertyIdStr);
                return null;
            }

            Category bigCategory = null;
            Set<Category> smallCategories = new LinkedHashSet<>();
            Admin admin = adminService.getByEmail("admin@test.com");
            Long adminId = admin.getId();

            try {
                if (!bigCategoryName.isEmpty() && !bigCategoryName.equals("-")) {
                    String firstBigCategory = bigCategoryName.split(",")[0].trim();
                    if (!firstBigCategory.isEmpty()) {
                        bigCategory = categoryService.findByName(firstBigCategory);
                    }
                }

                if (!smallCategoryName.isEmpty() && !smallCategoryName.equals("-")) {
                    String[] categoryNames = smallCategoryName.split(",");
                    for (String categoryName : categoryNames) {
                        String trimmedName = categoryName.trim();
                        if (!trimmedName.isEmpty()) {
                            Category category = categoryService.findByName(trimmedName);
                            if (category != null) {
                                smallCategories.add(category);
                            } else {
                                log.warn("소분류를 찾을 수 없음: {}", trimmedName);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("참조 데이터 조회 실패 - 오류: {}", e.getMessage());
            }

            PropertyStatus status = convertToPropertyStatus(statusStr);

            propertyRepository.insertPropertyWithId(
                Long.parseLong(propertyIdStr),
                adminId,
                buildingName.isEmpty() ? null : buildingName,
                status.name(),
                "공개".equals(isPublicStr),
                0L,
                bigCategory != null ? bigCategory.getId() : null,
                createdAt,
                updatedAt
            );

            Property property = propertyRepository.findById(Long.parseLong(propertyIdStr))
                .orElseThrow(() -> new RuntimeException("생성된 Property를 찾을 수 없습니다: " + propertyIdStr));

            AddressInformation addressInfo = createAddressInformation(row, property);
            if (addressInfo != null) {
                addressInfo = addressInfoRepository.save(addressInfo);
                property.setAddress(addressInfo);
            }
            String pnu = addressInfo != null ? addressInfo.getProperties().getPnu() : null;

            PriceInformation priceInfo = createPriceInformation(row, property);
            if (priceInfo != null) {
                priceInfo = priceInfoRepository.save(priceInfo);
                property.setPrice(priceInfo);
            }

            RegisterInformation registerInfo = RegisterInformation.builder()
                .properties(RegisterProperties.builder().build())
                .build();
            registerInfo = registerInfoRepository.save(registerInfo);
            property.setRegister(registerInfo);

            TemplateInformation templateInfo = TemplateInformation.builder()
                .properties(TemplateProperties.builder().build())
                .build();
            templateInfo = templateInfoRepository.save(templateInfo);
            property.setTemplate(templateInfo);

            LedgeInformation ledgeInfo = createLedgeInformation(row, property);
            if (ledgeInfo != null) {
                ledgeInfo = ledgeInfoRepository.save(ledgeInfo);
                property.getLedge().add(ledgeInfo);
            }

            LandInformation landInfo = createLandInformation(row, property);
            if (pnu != null) {
                try {
                    PropertyLandInfoResp landInfoApiResp = propertyInfoFacade.getLandInfo(pnu);
                    LandProperties land = landInfoApiResp.getLand();
                    landInfo.setProperties(land);
                } catch (Exception e) {
                    log.warn("토지정보 조회 실패 - PNU: {}, 오류: {}", pnu, e.getMessage());
                }
            }
            landInfo = landInfoRepository.save(landInfo);
            property.getLand().add(landInfo);

            if (pnu != null) {
                try {
                    List<PropertyFloorInfoItem> floorItems = floorApiPort.getFloorInfo(pnu).get();
                    List<PropertyFloorInfoItem> list = floorItems.stream()
                        .sorted(Comparator.nullsLast(Comparator.comparing(PropertyFloorInfoItem::getFloor)))
                        .toList();
                    for (int i = 0; i < list.size(); i++) {
                        PropertyFloorInfoItem item = list.get(i);
                        FloorInformation floorInfo = FloorInformation.builder()
                            .properties(
                                FloorProperties.builder()
                                    .floor(item.getFloor())
                                    .areaMeter(Math.floor(item.getAreaMeter()))
                                    .areaPyung(Math.floor(item.getAreaPyung()))
                                    .upjong(item.getUpjong())
                                    .build()
                            )
                            .buildingOrder(0L)
                            .rank((long) i)
                            .isPublic(true)
                            .build();

                        floorInfoRepository.save(floorInfo);
                        property.getFloors().add(floorInfo);
                    }
                } catch (Exception e) {
                    log.warn("층별 정보 처리 실패 - 매물번호: {}, PNU: {}, 오류: {}", property.getId(), pnu, e.getMessage());
                }
            }

            String detailContent = getCellValue(row, 41);
            if (detailContent.equals("-")) {
                detailContent = null;
            }

            Details details = Details.builder()
                .content(detailContent)
                .build();
            details.setProperty(property);
            detailsRepository.save(details);

            createMembers(row, property, adminId);

            try {
                if (!smallCategories.isEmpty()) {
                    property.getSmallCategories().addAll(smallCategories);
                }
            } catch (Exception e) {
                log.warn("소분류 설정 실패 - 매물번호: {}, 오류: {}", property.getId(), e.getMessage());
            }

            property = propertyRepository.save(property);
            propertyRepository.updatePropertyDates(property.getId(), createdAt, updatedAt);

            return property;

        } catch (Exception e) {
            log.error("행 파싱 중 오류 발생 - 행번호: {}, 오류: {}", row.getRowNum(), e.getMessage(), e);
            return null;
        }
    }

    private void createMembers(Row row, Property property, Long adminId) {
        for (int i = 0; i < 4; i++) {
            MemberInfo memberInfo = extractMemberInfo(row, i);
            if (memberInfo != null && memberInfo.hasValidData()) {
                Member customer = customerService.createCustomer(memberInfo, adminId);
                propertyMemberService.create(customer.getId(), property.getId());
            }
        }
    }

    private AddressInformation createAddressInformation(Row row, Property property) {
        String sido = getCellValue(row, 13);
        String gugun = getCellValue(row, 14);
        String dong = getCellValue(row, 15);
        String detail = getCellValue(row, 16);
        detail = extractAddressNumber(detail);

        if (sido.isEmpty() && gugun.isEmpty() && dong.isEmpty() && detail.isEmpty()) {
            return AddressInformation.builder()
                .properties(AddressProperties.builder().build())
                .build();
        }

        String jibunAddress = buildAddress(sido, gugun, dong, detail);

        String pnu = null;
        Double lat = null;
        Double lng = null;
        String roadAddress = null;
        Integer bun = null;
        Integer ji = null;

        try {
            log.info("카카오 주소 검색 시도: {}", jibunAddress);
            KakaoAddressSearchResponse kakao = kakaoApiService.searchAddress(jibunAddress);

            if (kakao != null && kakao.getDocuments() != null && !kakao.getDocuments().isEmpty()) {
                KakaoDocument document = kakao.getDocuments().get(0);
                String mountainNumber = "N".equals(document.getAddress().getMountainYn()) ? "1" : "2";
                String mainAddressNo = String.format("%04d",
                    Integer.parseInt(document.getAddress().getMainAddressNo()));
                String subAddressNo = String.format("%04d",
                    Integer.parseInt(
                        document.getAddress().getSubAddressNo() != null && !document.getAddress().getSubAddressNo().isEmpty()
                            ? document.getAddress().getSubAddressNo() : "0"));

                pnu = document.getAddress().getBCode() + mountainNumber + mainAddressNo + subAddressNo;
                lat = Double.parseDouble(document.getY());
                lng = Double.parseDouble(document.getX());

                PnuComponents pnuComponents = PnuComponents.parse(pnu);
                bun = Integer.valueOf(pnuComponents.getBun());
                ji = Integer.valueOf(pnuComponents.getJi());

                if (document.getRoadAddress() != null) {
                    roadAddress = document.getRoadAddress().getAddressName();
                }
            } else {
                log.warn("카카오 주소 검색 결과 없음: {}", jibunAddress);
            }
        } catch (Exception e) {
            log.warn("카카오 주소 검색 실패: {}, 오류: {}", jibunAddress, e.getMessage());
        }

        AddressProperties addressProps = AddressProperties.builder()
            .pnu(pnu)
            .jibunAddress(jibunAddress)
            .roadAddress(roadAddress)
            .jiCode(ji)
            .bunCode(bun)
            .lat(lat)
            .lng(lng)
            .build();

        return AddressInformation.builder()
            .properties(addressProps)
            .build();
    }

    private PriceInformation createPriceInformation(Row row, Property property) {
        String mmPriceStr = getCellValue(row, 19);
        String igPriceStr = getCellValue(row, 20);
        String etcPriceStr = getCellValue(row, 21);
        String roiStr = getCellValue(row, 22);
        String pyeongPriceStr = getCellValue(row, 23);
        String depositStr = getCellValue(row, 24);
        String monthlyRentStr = getCellValue(row, 25);
        String managementFeeStr = getCellValue(row, 26);
        String grOutStr = getCellValue(row, 27);
        String grEtcStr = getCellValue(row, 28);
        String loanStr = getCellValue(row, 29);

        Long mmPrice = parsePriceToLong(mmPriceStr);
        Long igPrice = parsePriceToLong(igPriceStr);
        Long etcPrice = parsePriceToLong(etcPriceStr);
        Double roi = parseDoubleValue(roiStr);
        Long pyeongPrice = parsePriceToLong(pyeongPriceStr);
        Long depositPrice = parsePriceToLong(depositStr);
        Long monthPrice = parsePriceToLong(monthlyRentStr);
        Long grPrice = parsePriceToLong(managementFeeStr);
        Long grOut = parsePriceToLong(grOutStr);
        Long grEtc = parsePriceToLong(grEtcStr);

        String landAreaPyeong = getCellValue(row, 31);
        String yeonAreaPyeong = getCellValue(row, 33);

        Double landPyeong = parseDoubleValue(landAreaPyeong);
        Double yeonPyeong = parseDoubleValue(yeonAreaPyeong);

        Long yeonPyeongPrice = null;
        if (pyeongPrice != null && landPyeong != null && yeonPyeong != null && yeonPyeong > 0) {
            try {
                double calculatedPrice = (pyeongPrice * landPyeong) / yeonPyeong;
                yeonPyeongPrice = Math.round(calculatedPrice);
            } catch (ArithmeticException e) {
                yeonPyeongPrice = null;
            }
        }

        if (mmPrice == null && igPrice == null && etcPrice == null && depositPrice == null &&
            monthPrice == null && grPrice == null && grOut == null && grEtc == null) {
            return PriceInformation.builder()
                .properties(PriceProperties.builder().build())
                .build();
        }

        PriceProperties priceProps = PriceProperties.builder()
            .mmPrice(mmPrice)
            .igPrice(igPrice)
            .etcPrice(etcPrice)
            .roi(roi)
            .yeonPyongPrice(yeonPyeongPrice)
            .pyeongPrice(pyeongPrice)
            .depositPrice(depositPrice)
            .monthPrice(monthPrice)
            .grPrice(grPrice)
            .grOut(grOut)
            .grEtc(grEtc)
            .loanDescription(loanStr.isEmpty() ? null : loanStr)
            .build();

        return PriceInformation.builder()
            .properties(priceProps)
            .build();
    }

    private LedgeInformation createLedgeInformation(Row row, Property property) {
        String undergroundStr = getCellValue(row, 17);
        String abovegroundStr = getCellValue(row, 18);
        String landAreaMeter = getCellValue(row, 30);
        String landAreaPyeong = getCellValue(row, 31);
        String yeonAreaMeter = getCellValue(row, 32);
        String yeonAreaPyeong = getCellValue(row, 33);
        String buildingAreaMeter = getCellValue(row, 34);
        String buildingAreaPyeong = getCellValue(row, 35);
        String geonPyeStr = getCellValue(row, 47);
        String yongjeokStr = getCellValue(row, 48);
        String structureStr = getCellValue(row, 50);
        String heightStr = getCellValue(row, 51);
        String jiboongStr = getCellValue(row, 52);
        String jajuInStr = getCellValue(row, 53);
        String jajuOutStr = getCellValue(row, 54);
        String kigyeInStr = getCellValue(row, 55);
        String kigyeOutStr = getCellValue(row, 56);
        String ilbanElevatorStr = getCellValue(row, 57);
        String hwamoolElevatorStr = getCellValue(row, 58);
        String saedaeStr = getCellValue(row, 59);
        String gaguStr = getCellValue(row, 60);
        String heogaeDateStr = getCellValue(row, 61);
        String chakGongDateStr = getCellValue(row, 62);
        String sengInDateStr = getCellValue(row, 63);
        String remodelDateStr = getCellValue(row, 64);

        Integer minFloor = parseIntegerValue(undergroundStr);
        Integer maxFloor = parseIntegerValue(abovegroundStr);
        Double landMeter = parseDoubleValue(landAreaMeter);
        Double landPyeong = parseDoubleValue(landAreaPyeong);
        Double yeonMeter = parseDoubleValue(yeonAreaMeter);
        Double yeonPyeong = parseDoubleValue(yeonAreaPyeong);
        Double buildingMeter = parseDoubleValue(buildingAreaMeter);
        Double buildingPyeong = parseDoubleValue(buildingAreaPyeong);
        Double geonPye = parseDoubleValue(geonPyeStr);
        Double yongjeok = parseDoubleValue(yongjeokStr);
        Double height = parseDoubleValue(heightStr);
        Integer jajuIn = parseIntegerValue(jajuInStr);
        Integer jajuOut = parseIntegerValue(jajuOutStr);
        Integer kigyeIn = parseIntegerValue(kigyeInStr);
        Integer kigyeOut = parseIntegerValue(kigyeOutStr);
        Integer ilbanElevator = parseIntegerValue(ilbanElevatorStr);
        Integer hwamoolElevator = parseIntegerValue(hwamoolElevatorStr);
        Integer saedae = parseIntegerValue(saedaeStr);
        Integer gagu = parseIntegerValue(gaguStr);
        LocalDate heogaeDate = parseLocalDate(heogaeDateStr);
        LocalDate chakGongDate = parseLocalDate(chakGongDateStr);
        LocalDate sengInDate = parseLocalDate(sengInDateStr);
        LocalDate remodelDate = parseLocalDate(remodelDateStr);

        if (minFloor == null && maxFloor == null && landMeter == null && yeonMeter == null &&
            buildingMeter == null && structureStr.isEmpty() && saedae == null && gagu == null) {
            return LedgeInformation.builder()
                .properties(LedgerProperties.builder().build())
                .build();
        }

        String sido = getCellValue(row, 13);
        String gugun = getCellValue(row, 14);
        String dong = getCellValue(row, 15);
        String detail = getCellValue(row, 16);
        String jibunAddress = buildAddress(sido, gugun, dong, detail);

        LedgerProperties ledgerProps = LedgerProperties.builder()
            .jibunAddress(jibunAddress)
            .minFloor(minFloor)
            .maxFloor(maxFloor)
            .landAreaMeter(landMeter)
            .landAreaPyeong(landPyeong)
            .yeonAreaMeter(yeonMeter)
            .yeonAreaPyeong(yeonPyeong)
            .buildingAreaMeter(buildingMeter)
            .buildingAreaPyeong(buildingPyeong)
            .structure(structureStr.isEmpty() ? null : structureStr)
            .geonPye(geonPye)
            .yongjeok(yongjeok)
            .height(height)
            .jiboong(jiboongStr.isEmpty() ? null : jiboongStr)
            .jajuInParking(jajuIn)
            .jajuOutParking(jajuOut)
            .kigyeInParking(kigyeIn)
            .kigyeOutParking(kigyeOut)
            .ilbanElevator(ilbanElevator)
            .hwamoolElevator(hwamoolElevator)
            .saedae(saedae)
            .gagu(gagu)
            .heogaeDate(heogaeDate)
            .chakGongDate(chakGongDate)
            .sengInDate(sengInDate)
            .remodelDate(remodelDate)
            .build();

        return LedgeInformation.builder()
            .properties(ledgerProps)
            .buildingOrder(0L)
            .build();
    }

    private LandInformation createLandInformation(Row row, Property property) {
        String jimokStr = getCellValue(row, 45);
        String yongdoStr = getCellValue(row, 46);

        if (jimokStr.isEmpty() && yongdoStr.isEmpty()) {
            return LandInformation.builder()
                .properties(LandProperties.builder().build())
                .build();
        }

        String sido = getCellValue(row, 13);
        String gugun = getCellValue(row, 14);
        String dong = getCellValue(row, 15);
        String detail = getCellValue(row, 16);
        String jibunAddress = buildAddress(sido, gugun, dong, detail);

        LandProperties landProps = LandProperties.builder()
            .jibunAddress(jibunAddress)
            .jimok(jimokStr.isEmpty() ? null : jimokStr)
            .yongdo(yongdoStr.isEmpty() ? null : yongdoStr)
            .build();

        return LandInformation.builder()
            .properties(landProps)
            .buildingOrder(0L)
            .build();
    }

    private MemberInfo extractMemberInfo(Row row, int memberIndex) {
        int baseCol = 65 + (memberIndex * 8);
        String memberName = getCellValue(row, baseCol);

        if (memberName == null || memberName.trim().isEmpty() || "-".equals(memberName.trim())) {
            return null;
        }

        String memberType = getCellValue(row, baseCol + 1);
        String memberPhone = getCellValue(row, baseCol + 3);
        String memberHomePhone = getCellValue(row, baseCol + 4);
        String memberFunnel = getCellValue(row, baseCol + 6);
        String memberMemo = getCellValue(row, baseCol + 8);

        return MemberInfo.builder()
            .name(memberName.trim())
            .type(isValidValue(memberType) ? memberType.trim() : null)
            .phone(isValidValue(memberPhone) ? memberPhone.trim() : null)
            .homePhone(isValidValue(memberHomePhone) ? memberHomePhone.trim() : null)
            .funnel(isValidValue(memberFunnel) ? memberFunnel.trim() : null)
            .memo(isValidValue(memberMemo) ? memberMemo.trim() : null)
            .build();
    }

    private boolean isValidValue(String value) {
        return value != null && !value.trim().isEmpty() && !"-".equals(value.trim());
    }

    private PropertyStatus convertToPropertyStatus(String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) {
            return null;
        }
        switch (statusStr) {
            case "준비": return PropertyStatus.READY;
            case "완료": return PropertyStatus.COMPLETE;
            case "보류": return PropertyStatus.PENDING;
            case "매각": return PropertyStatus.SOLD;
            default:
                log.warn("알 수 없는 상태값: {}", statusStr);
                return null;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || dateTimeStr.equals("-")) {
            return LocalDateTime.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}, 현재 시간으로 대체", dateTimeStr);
            return LocalDateTime.now();
        }
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equals("-")) {
            return null;
        }
        try {
            dateStr = dateStr.trim();
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(dateStr);
            }
            if (dateStr.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }
            if (dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            }
            if (dateStr.matches("\\d{8}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            if (dateStr.matches("\\d{6}")) {
                return LocalDate.parse(dateStr + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            if (dateStr.matches("\\d{4}")) {
                return LocalDate.parse(dateStr + "0101", DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            log.warn("지원하지 않는 날짜 형식: {}", dateStr);
            return null;
        } catch (DateTimeParseException e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    private String buildAddress(String sido, String gugun, String dong, String detail) {
        StringBuilder address = new StringBuilder();
        if (!sido.isEmpty() && !sido.equals("-")) address.append(sido);
        if (!gugun.isEmpty() && !gugun.equals("-")) {
            if (address.length() > 0) address.append(" ");
            address.append(gugun);
        }
        if (!dong.isEmpty() && !dong.equals("-")) {
            if (address.length() > 0) address.append(" ");
            address.append(dong);
        }
        if (!detail.isEmpty() && !detail.equals("-")) {
            if (address.length() > 0) address.append(" ");
            address.append(detail);
        }
        return address.toString();
    }

    private Long parsePriceToLong(String priceStr) {
        if (priceStr == null || priceStr.isEmpty() || priceStr.equals("-")) return null;
        try {
            String cleanPrice = priceStr.replaceAll("[^0-9.]", "");
            if (cleanPrice.isEmpty()) return null;
            double doubleValue = Double.parseDouble(cleanPrice);
            return Math.round(doubleValue);
        } catch (Exception e) {
            log.warn("가격 파싱 실패: {}", priceStr);
            return null;
        }
    }

    private Integer parseIntegerValue(String intStr) {
        if (intStr == null || intStr.isEmpty() || intStr.equals("-")) return null;
        try {
            String cleanInt = intStr.replaceAll("[^0-9]", "");
            if (cleanInt.isEmpty()) return null;
            return Integer.valueOf(cleanInt);
        } catch (NumberFormatException e) {
            log.warn("정수 파싱 실패: {}", intStr);
            return null;
        }
    }

    private Double parseDoubleValue(String doubleStr) {
        if (doubleStr == null || doubleStr.isEmpty() || doubleStr.equals("-")) return null;
        try {
            String cleanDouble = doubleStr.replaceAll("[^0-9.]", "");
            if (cleanDouble.isEmpty()) return null;
            return Double.valueOf(cleanDouble);
        } catch (NumberFormatException e) {
            log.warn("실수 파싱 실패: {}", doubleStr);
            return null;
        }
    }

    private String extractAddressNumber(String detail) {
        if (detail == null || detail.trim().isEmpty()) return detail;
        String trimmed = detail.trim();
        return trimmed.replaceAll("^(\\d+(?:-\\d+)?).*", "$1");
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
