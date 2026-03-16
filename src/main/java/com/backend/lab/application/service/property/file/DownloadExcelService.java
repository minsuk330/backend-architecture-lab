package com.backend.lab.application.service.property.file;

import com.backend.lab.application.port.in.property.file.DownloadExcelUseCase;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.memberNote.entity.MemberNote;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.repository.PropertyRepository;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.pnuTable.entity.PnuTable;
import com.backend.lab.domain.property.pnuTable.entity.vo.PnuTableType;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.service.SecretService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DownloadExcelService implements DownloadExcelUseCase {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final AdminService adminService;
    private final DetailsService detailsService;
    private final SecretService secretService;
    private final PnuTableService pnuTableService;
    private final PropertyMemberService propertyMemberService;
    private final MemberService memberService;
    private final MemberNoteService memberNoteService;

    @Override
    public ByteArrayInputStream createExcelFile(Long adminId, List<Long> ids) {
        long startTime = System.currentTimeMillis();
        log.info("=== Excel Generation Started === AdminId: {}, IdsCount: {}",
            adminId, ids != null ? ids.size() : "ALL");

        List<Property> properties;
        if (ids != null && !ids.isEmpty()) {
            long queryStart = System.currentTimeMillis();
            properties = propertyRepository.findByIdInWithAllDetails(ids).stream()
                .sorted(Comparator.comparing(Property::getId))
                .toList();
            long queryEnd = System.currentTimeMillis();
            log.info("Specific IDs Query completed: {}ms, Records: {}",
                queryEnd - queryStart, properties.size());
        } else {
            long queryStart = System.currentTimeMillis();
            properties = getAllPropertiesWithPaging();
            long queryEnd = System.currentTimeMillis();
            log.info("Paging Query completed: {}ms, Records: {}",
                queryEnd - queryStart, properties.size());
        }

        final String[] PROPERTY_HEADERS = {
            "매물번호", "공개여부", "진행상태", "담당자명", "건물명", "대분류", "소분류", "거래유형",
            "등록일", "수정일", "시/도", "구/군", "동/읍/면", "상세주소",
            "매매가(만원)", "입금가(만원)", "기타매매가(만원)", "수익률(%)", "평단가(만원)",
            "보증금(만원)", "월임대료(만원)", "관리비(만원)", "관리비 지출(만원)", "관리비 기타(만원)", "대출현황",
            "상세정보", "비밀메모",
            "기본토지정보", "기본토지용도",
            "지하층", "지상층",
            "대지면적(㎡)", "대지면적(평)", "연면적(㎡)", "연면적(평)", "건축면적(㎡)", "건축면적(평)",
            "건폐율", "용적률", "주용도", "주구조", "건물 높이", "지붕구조",
            "주차(자주식,옥내)", "주차(자주식,옥외)", "주차(기계식,옥내)", "주차(기계식,옥외)", "주차(전치가,옥내)", "주차(전치가,옥외)",
            "엘리베이터(일반)", "엘리베이터(화물)",
            "세대수", "가구수",
            "허가일", "착공일", "사용승인일", "리모델링일",
            "고객명", "고객분류", "고객휴대전화", "고객자택번호", "고객유입경로", "고객메모",
            "고객명2", "고객분류2", "고객휴대전화2", "고객자택번호2", "고객유입경로2", "고객메모2",
            "고객명3", "고객분류3", "고객휴대전화3", "고객자택번호3", "고객유입경로3", "고객메모3",
            "고객명4", "고객분류4", "고객휴대전화4", "고객자택번호4", "고객유입경로4", "고객메모4",
            "고객명5", "고객분류5", "고객휴대전화5", "고객자택번호5", "고객유입경로5", "고객메모5",
            "고객명6", "고객분류6", "고객휴대전화6", "고객자택번호6", "고객유입경로6", "고객메모6",
            "고객명7", "고객분류7", "고객휴대전화7", "고객자택번호7", "고객유입경로7", "고객메모7",
            "고객명8", "고객분류8", "고객휴대전화8", "고객자택번호8", "고객유입경로8", "고객메모8",
            "고객명9", "고객분류9", "고객휴대전화9", "고객자택번호9", "고객유입경로9", "고객메모9",
            "고객명10", "고객분류10", "고객휴대전화10", "고객자택번호10", "고객유입경로10", "고객메모10"
        };

        long excelStart = System.currentTimeMillis();
        log.info("Starting Excel generation for {} properties", properties.size());

        try (Workbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("properties");

            long headerStart = System.currentTimeMillis();
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < PROPERTY_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(PROPERTY_HEADERS[i]);
            }
            long headerEnd = System.currentTimeMillis();
            log.info("Header creation completed: {}ms", headerEnd - headerStart);

            List<Long> propertyIds = properties.stream()
                .map(Property::getId)
                .filter(Objects::nonNull)
                .toList();

            List<Long> adminIds = new ArrayList<>(properties.stream()
                .map(Property::getAdminId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());

            adminIds.add(adminId);

            Map<Long, Admin> adminMap = adminService.gets(adminIds).stream()
                .collect(Collectors.toMap(Admin::getId, Function.identity()));

            List<PnuTable> pnus = pnuTableService.gets();
            Map<String, String> sidoCache = pnus.stream()
                .filter(pnu -> pnu.getType() == PnuTableType.SIDO)
                .collect(Collectors.toMap(PnuTable::getSido, PnuTable::getSidoName,
                    (existing, replacement) -> existing));
            Map<String, String> sigunguCache = pnus.stream()
                .filter(pnu -> pnu.getType() == PnuTableType.SIGUNGU)
                .collect(Collectors.toMap(
                    pnu -> pnu.getSido() + pnu.getSigungu(),
                    PnuTable::getSigunguName,
                    (existing, replacement) -> existing));
            Map<String, String> bjdongCache = pnus.stream()
                .filter(pnu -> pnu.getType() == PnuTableType.BJDONG)
                .collect(Collectors.toMap(
                    pnu -> pnu.getSido() + pnu.getSigungu() + pnu.getBjd(),
                    PnuTable::getBjdName,
                    (existing, replacement) -> existing));

            Map<Long, Details> detailsMap = detailsService.getsByPropertyId(propertyIds).stream()
                .distinct()
                .collect(Collectors.toMap(dt -> dt.getProperty().getId(), Function.identity()));

            Map<Long, List<Secret>> secretsMap = secretService.getsByPropertyIds(propertyIds).stream()
                .collect(Collectors.groupingBy(secret -> secret.getProperty().getId()));

            Map<Long, List<PropertyMember>> pmMap = propertyMemberService.getsByPropertyIds(propertyIds).stream()
                .collect(Collectors.groupingBy(PropertyMember::getPropertyId));

            Set<Long> memberIds = pmMap.values().stream()
                .flatMap(List::stream)
                .map(PropertyMember::getMemberId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            Map<Long, Member> memberMap = memberService.gets(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

            Map<Long, List<MemberNote>> memberNoteMap = memberNoteService.getsByMemberIn(memberIds).stream()
                .collect(Collectors.groupingBy(mn -> mn.getMember().getId()));

            long dataStart = System.currentTimeMillis();
            for (int i = 0; i < properties.size(); i++) {
                Property property = properties.get(i);
                int rowNum = i + 1;
                Row row = sheet.createRow(rowNum);

                if (i > 0 && i % 1000 == 0) {
                    long currentTime = System.currentTimeMillis();
                    log.info("Excel row processing: {}/{} completed, {}ms elapsed",
                        i, properties.size(), currentTime - dataStart);
                }

                int cellNum = 0;
                setCellValueSafely(row, cellNum++, () -> String.valueOf(property.getId()));
                setCellValueSafely(row, cellNum++, () -> property.getIsPublic() ? "공개" : "비공개");
                setCellValueSafely(row, cellNum++, () -> property.getStatus().getDisplayName());

                setCellValueSafely(row, cellNum++, () -> {
                    Long aId = property.getAdminId();
                    if (aId != null) {
                        Admin admin = adminMap.get(aId);
                        return admin == null ? "" : admin.getName();
                    }
                    return "";
                });

                setCellValueSafely(row, cellNum++, property::getBuildingName);
                setCellValueSafely(row, cellNum++, () -> property.getBigCategory().getName());
                setCellValueSafely(row, cellNum++, () ->
                    property.getSmallCategories().stream()
                        .map(category -> category.getName())
                        .collect(Collectors.joining(", ")));
                setCellValueSafely(row, cellNum++, () -> "매매");

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                setCellValueSafely(row, cellNum++, () -> dateTimeFormatter.format(property.getCreatedAt()));
                setCellValueSafely(row, cellNum++, () -> dateTimeFormatter.format(property.getUpdatedAt()));

                AddressProperties address = property.getAddress().getProperties();
                if (address.getPnu() != null) {
                    PnuComponents pnu = PnuComponents.parse(address.getPnu());
                    setCellValueSafely(row, cellNum++,
                        () -> getSidoNameFromCache(pnu.getSigunguCd().substring(0, 2), sidoCache));
                    setCellValueSafely(row, cellNum++,
                        () -> getSigunguNameFromCache(pnu.getSigunguCd().substring(0, 2),
                            pnu.getSigunguCd().substring(2, 5), sigunguCache));
                    setCellValueSafely(row, cellNum++,
                        () -> getBjdongNameFromCache(pnu.getSigunguCd().substring(0, 2),
                            pnu.getSigunguCd().substring(2, 5), pnu.getBjdongCd(), bjdongCache));
                }
                setCellValueSafely(row, cellNum++, address::getRoadAddress);

                PriceProperties price = property.getPrice().getProperties();
                if (price == null) {
                    price = new PriceProperties();
                }
                final PriceProperties finalPrice = price;
                setCellValueSafely(row, cellNum++, finalPrice::getMmPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getIgPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getEtcPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getRoi);
                setCellValueSafely(row, cellNum++, finalPrice::getPyeongPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getDepositPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getMonthPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getGrPrice);
                setCellValueSafely(row, cellNum++, finalPrice::getGrOut);
                setCellValueSafely(row, cellNum++, finalPrice::getGrEtc);
                setCellValueSafely(row, cellNum++, finalPrice::getLoanDescription);

                Details detail = detailsMap.get(property.getId());
                setCellValueSafely(row, cellNum++, detail::getContent);

                StringBuilder secretStr = new StringBuilder();
                List<Secret> secrets = secretsMap.get(property.getId());
                if (secrets == null) {
                    secrets = List.of();
                }

                Admin admin = adminMap.get(adminId);
                if (admin.getRole() != null && admin.getRole().equals(AdminRole.ADMIN)) {
                    for (Secret secret : secrets) {
                        secretStr.append(getMultipleLineStr(secret::getContent));
                    }
                } else {
                    Department department = admin.getDepartment();
                    Permission permission = admin.getPermission();
                    SecretMemoExposeLevel level = permission != null ? permission.getSecretMemoExposeLevel() : null;
                    for (Secret secret : secretService.filter(adminId, department, level, secrets)) {
                        secretStr.append(getMultipleLineStr(secret::getContent));
                    }
                }
                setCellValueSafely(row, cellNum++, secretStr::toString);

                List<LandInformation> lands = property.getLand();
                StringBuilder jimok = new StringBuilder();
                StringBuilder yongdo = new StringBuilder();
                for (LandInformation land : lands) {
                    LandProperties lp = land.getProperties();
                    if (lp != null) {
                        jimok.append(getMultipleLineStr(lp::getJimok));
                        yongdo.append(getMultipleLineStr(lp::getYongdo));
                    }
                }
                setCellValueSafely(row, cellNum++, jimok::toString);
                setCellValueSafely(row, cellNum++, yongdo::toString);

                List<LedgeInformation> ledges = property.getLedge();
                StringBuilder minFloorStr = new StringBuilder();
                StringBuilder maxFloorStr = new StringBuilder();
                StringBuilder landAreaMeter = new StringBuilder();
                StringBuilder landAreaPyeong = new StringBuilder();
                StringBuilder yeonAreaMeter = new StringBuilder();
                StringBuilder yeonAreaPyeong = new StringBuilder();
                StringBuilder buildingAreaMeter = new StringBuilder();
                StringBuilder buildingAreaPyeong = new StringBuilder();
                StringBuilder geonPye = new StringBuilder();
                StringBuilder yongjeok = new StringBuilder();
                StringBuilder mainPurpsCdNm = new StringBuilder();
                StringBuilder structure = new StringBuilder();
                StringBuilder heights = new StringBuilder();
                StringBuilder jiboong = new StringBuilder();
                StringBuilder jajuInParking = new StringBuilder();
                StringBuilder jajuOutParking = new StringBuilder();
                StringBuilder kigyeInParking = new StringBuilder();
                StringBuilder kigyeOutParking = new StringBuilder();
                StringBuilder elcInParking = new StringBuilder();
                StringBuilder elcOutParking = new StringBuilder();
                StringBuilder ilbanElevator = new StringBuilder();
                StringBuilder hwamoolElevator = new StringBuilder();
                StringBuilder saedae = new StringBuilder();
                StringBuilder gagu = new StringBuilder();
                StringBuilder heogaeDate = new StringBuilder();
                StringBuilder chakGongDate = new StringBuilder();
                StringBuilder sengInDate = new StringBuilder();
                StringBuilder remodelDate = new StringBuilder();

                for (LedgeInformation ledge : ledges) {
                    LedgerProperties tmpLp = ledge.getProperties();
                    if (tmpLp == null) {
                        tmpLp = new LedgerProperties();
                    }
                    final LedgerProperties lp = tmpLp;

                    maxFloorStr.append(getMultipleLineStr(lp::getMaxFloor));
                    minFloorStr.append(getMultipleLineStr(lp::getMinFloor));
                    landAreaMeter.append(getMultipleLineStr(lp::getLandAreaMeter));
                    landAreaPyeong.append(getMultipleLineStr(lp::getLandAreaPyeong));
                    yeonAreaMeter.append(getMultipleLineStr(lp::getYeonAreaMeter));
                    yeonAreaPyeong.append(getMultipleLineStr(lp::getYeonAreaPyeong));
                    buildingAreaMeter.append(getMultipleLineStr(lp::getBuildingAreaMeter));
                    buildingAreaPyeong.append(getMultipleLineStr(lp::getBuildingAreaPyeong));
                    geonPye.append(getMultipleLineStr(lp::getGeonPye));
                    yongjeok.append(getMultipleLineStr(lp::getYongjeok));
                    mainPurpsCdNm.append(getMultipleLineStr(lp::getMainPurpsCdNm));
                    structure.append(getMultipleLineStr(lp::getStructure));
                    heights.append(getMultipleLineStr(lp::getHeight));
                    jiboong.append(getMultipleLineStr(lp::getJiboong));
                    jajuInParking.append(getMultipleLineStr(lp::getJajuInParking));
                    jajuOutParking.append(getMultipleLineStr(lp::getJajuOutParking));
                    kigyeInParking.append(getMultipleLineStr(lp::getKigyeInParking));
                    kigyeOutParking.append(getMultipleLineStr(lp::getKigyeOutParking));
                    elcInParking.append(getMultipleLineStr(lp::getElcInParking));
                    elcOutParking.append(getMultipleLineStr(lp::getElcOutParking));
                    ilbanElevator.append(getMultipleLineStr(lp::getIlbanElevator));
                    hwamoolElevator.append(getMultipleLineStr(lp::getHwamoolElevator));
                    saedae.append(getMultipleLineStr(lp::getSaedae));
                    gagu.append(getMultipleLineStr(lp::getGagu));
                    heogaeDate.append(getMultipleLineStr(() -> dateFormatter.format(lp.getHeogaeDate())));
                    chakGongDate.append(getMultipleLineStr(() -> dateFormatter.format(lp.getChakGongDate())));
                    sengInDate.append(getMultipleLineStr(() -> dateFormatter.format(lp.getSengInDate())));
                    remodelDate.append(getMultipleLineStr(() -> dateFormatter.format(lp.getRemodelDate())));
                }

                setCellValueSafely(row, cellNum++, minFloorStr::toString);
                setCellValueSafely(row, cellNum++, maxFloorStr::toString);
                setCellValueSafely(row, cellNum++, landAreaMeter::toString);
                setCellValueSafely(row, cellNum++, landAreaPyeong::toString);
                setCellValueSafely(row, cellNum++, yeonAreaMeter::toString);
                setCellValueSafely(row, cellNum++, yeonAreaPyeong::toString);
                setCellValueSafely(row, cellNum++, buildingAreaMeter::toString);
                setCellValueSafely(row, cellNum++, buildingAreaPyeong::toString);
                setCellValueSafely(row, cellNum++, geonPye::toString);
                setCellValueSafely(row, cellNum++, yongjeok::toString);
                setCellValueSafely(row, cellNum++, mainPurpsCdNm::toString);
                setCellValueSafely(row, cellNum++, structure::toString);
                setCellValueSafely(row, cellNum++, heights::toString);
                setCellValueSafely(row, cellNum++, jiboong::toString);
                setCellValueSafely(row, cellNum++, jajuInParking::toString);
                setCellValueSafely(row, cellNum++, jajuOutParking::toString);
                setCellValueSafely(row, cellNum++, kigyeInParking::toString);
                setCellValueSafely(row, cellNum++, kigyeOutParking::toString);
                setCellValueSafely(row, cellNum++, elcInParking::toString);
                setCellValueSafely(row, cellNum++, elcOutParking::toString);
                setCellValueSafely(row, cellNum++, ilbanElevator::toString);
                setCellValueSafely(row, cellNum++, hwamoolElevator::toString);
                setCellValueSafely(row, cellNum++, saedae::toString);
                setCellValueSafely(row, cellNum++, gagu::toString);
                setCellValueSafely(row, cellNum++, heogaeDate::toString);
                setCellValueSafely(row, cellNum++, chakGongDate::toString);
                setCellValueSafely(row, cellNum++, sengInDate::toString);
                setCellValueSafely(row, cellNum++, remodelDate::toString);

                List<PropertyMember> propertyMembers = pmMap.get(property.getId());
                if (propertyMembers == null) {
                    propertyMembers = List.of();
                }

                for (int j = 0; j < propertyMembers.size() && j < 10; j++) {
                    PropertyMember pm = propertyMembers.get(j);
                    Long memberId = pm.getMemberId();
                    Member member = memberMap.get(memberId);
                    CustomerProperties customer = member.getCustomerProperties();
                    AgentProperties agent = member.getAgentProperties();
                    SellerProperties seller = member.getSellerProperties();
                    BuyerProperties buyer = member.getBuyerProperties();

                    String memberName = "";
                    String memberType = member.getType().getDisplayName();
                    String memberPhone = "";
                    String memberHomePhone = "";
                    String funnel = "";
                    StringBuilder memo = new StringBuilder();

                    List<MemberNote> notes = memberNoteMap.get(memberId);
                    if (notes == null) {
                        notes = List.of();
                    }
                    for (MemberNote note : notes) {
                        memo.append(getMultipleLineStr(note::getContent));
                    }

                    switch (member.getType()) {
                        case AGENT:
                            memberName = getValSafely(agent::getName);
                            memberPhone = getValSafely(agent::getPhoneNumber);
                            break;
                        case SELLER:
                            memberName = getValSafely(seller::getName);
                            memberPhone = getValSafely(seller::getPhoneNumber);
                            break;
                        case CUSTOMER:
                            memberName = getValSafely(customer::getName);
                            memberPhone = getValSafely(customer::getPhoneNumber);
                            memberHomePhone = getValSafely(customer::getHomePhoneNumber);
                            funnel = getValSafely(() -> customer.getFunnel().getDisplayName());
                            break;
                        case BUYER:
                            memberName = getValSafely(buyer::getName);
                            memberPhone = getValSafely(buyer::getPhoneNumber);
                            break;
                    }

                    String finalMemberName = memberName;
                    String finalMemberPhone = memberPhone;
                    String finalMemberHomePhone = memberHomePhone;
                    String finalFunnel = funnel;
                    setCellValueSafely(row, cellNum++, () -> finalMemberName);
                    setCellValueSafely(row, cellNum++, () -> memberType);
                    setCellValueSafely(row, cellNum++, () -> finalMemberPhone);
                    setCellValueSafely(row, cellNum++, () -> finalMemberHomePhone);
                    setCellValueSafely(row, cellNum++, () -> finalFunnel);
                    setCellValueSafely(row, cellNum++, memo::toString);
                }
            }

            long dataEnd = System.currentTimeMillis();
            log.info("Excel data rows completed: {}ms, Total rows: {}",
                dataEnd - dataStart, properties.size());

            long writeStart = System.currentTimeMillis();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            long writeEnd = System.currentTimeMillis();

            log.info("Excel file write completed: {}ms, File size: {}MB",
                writeEnd - writeStart, out.size() / 1024 / 1024);

            long totalTime = System.currentTimeMillis() - startTime;
            log.info("=== Excel Generation Completed === Total time: {}ms", totalTime);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.ERROR_WHITE_PROCESS_XLSX, e.getMessage());
        }
    }

    private List<Property> getAllPropertiesWithPaging() {
        long startTime = System.currentTimeMillis();
        List<Property> allProperties = new ArrayList<>();
        int batchSize = 1000;
        int page = 0;

        log.info("Starting paging query with batch size: {}", batchSize);

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize);
            Page<Property> propertyPage = propertyRepository.findAllWithBasicDetails(pageable);

            allProperties.addAll(propertyPage.getContent());

            if (!propertyPage.hasNext()) {
                break;
            }
            page++;
        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Paging query completed: Total {}ms, Records: {}", totalTime, allProperties.size());

        return allProperties;
    }

    private String getSidoNameFromCache(String sidoCode, Map<String, String> sidoCache) {
        return sidoCode != null ? sidoCache.get(sidoCode) : null;
    }

    private String getSigunguNameFromCache(String sidoCode, String sigunguCode, Map<String, String> sigunguCache) {
        if (sidoCode == null || sigunguCode == null) return null;
        return sigunguCache.get(sidoCode + sigunguCode);
    }

    private String getBjdongNameFromCache(String sidoCode, String sigunguCode, String bjdongCode, Map<String, String> bjdongCache) {
        if (sidoCode == null || sigunguCode == null || bjdongCode == null) return null;
        return bjdongCache.get(sidoCode + sigunguCode + bjdongCode);
    }

    private void setCellValueSafely(Row row, int cellNum, Supplier<Object> valueSupplier) {
        try {
            row.createCell(cellNum).setCellValue(String.valueOf(valueSupplier.get()));
        } catch (Exception e) {
            row.createCell(cellNum).setCellValue("");
        }
    }

    private String getValSafely(Supplier<Object> supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (Exception e) {
            return "";
        }
    }

    private String getMultipleLineStr(Supplier<Object> supplier) {
        return getValSafely(supplier) + "\n";
    }
}
