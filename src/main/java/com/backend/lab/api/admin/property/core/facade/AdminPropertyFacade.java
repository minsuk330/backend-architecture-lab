package com.backend.lab.api.admin.property.core.facade;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
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
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
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
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyWorkLogService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.service.SecretService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
@Transactional(readOnly = true)
@Slf4j
public class AdminPropertyFacade {

  private final PropertyService propertyService;
  private final SecretService secretService;
  private final AdminService adminService;
  private final DetailsService detailsService;
  private final TaskNoteService taskNoteService;
  private final UploadFileService uploadFileService;

  private final PropertyMapper propertyMapper;
  private final MemberService memberService;
  private final SaleService saleService;
  private final PnuTableService pnuTableService;
  private final PropertyMemberService propertyMemberService;
  private final MemberNoteService memberNoteService;
  private final PropertyRepository propertyRepository;
  private final PropertyWorkLogService propertyWorkLogService;

  public PageResp<PropertySearchResp> getsWithPropertyMember(Long memberId, PageOptions options) {
    Page<Property> page = propertyService.getsWithPropertyMember(memberId, options.pageable());
    List<PropertySearchResp> data = page.getContent().stream()
        .map(propertyMapper::propertySearchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public PageResp<PropertySearchResp> getsByAgentAdv(Long id, PageOptions options) {
    Member agent = memberService.getById(id);
    Page<Property> page = propertyService.getsByAgentAdvAvailable(agent, options.pageable());
    List<PropertySearchResp> data = page.getContent().stream()
        .map(propertyMapper::propertySearchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public PageResp<PropertySearchResp> getsByExclusiveAgentId(Long id, PageOptions options) {
    Page<Property> page = propertyService.getsByExclusiveAgentId(id, options.pageable());
    List<PropertySearchResp> data = page.getContent().stream()
        .map(propertyMapper::propertySearchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  // ***************************** Deleted Property *****************************
  public PageResp<PropertySearchResp> searchDeleted(SearchPropertyOptions options,
      List<Long> adminIds, List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    Page<Property> search = propertyService.searchDeleted(options, adminIds, bigCategoryIds,
        smallCategoryIds);
    List<PropertySearchResp> list = search.stream().map(
        propertyMapper::propertySearchResp
    ).toList();

    return new PageResp<>(search, list);
  }

  public PropertyResp getDeletedById(Long propertyId, Long userId) {
    Property property = propertyService.getDeletedById(propertyId);

    Details details = detailsService.getByPropertyId(propertyId);

    List<UploadFileResp> propertyImages = details != null && details.getPropertyImages() != null
        ? details.getPropertyImages().stream().map(uploadFileService::uploadFileResp).toList()
        : new ArrayList<>();

    List<UploadFileResp> dataImages = details != null && details.getDataImages() != null
        ? details.getDataImages().stream().map(uploadFileService::uploadFileResp).toList()
        : new ArrayList<>();

    Sale sale = saleService.getByProperty(propertyId);

    return propertyService.propertyResp(property,
        propertyMapper.propertyAdminResp(property),
        propertyMapper.propertyDefaultResp(property, null),
        propertyMapper.propertyPriceResp(property),
        propertyMapper.propertyLedgeResp(property),
        propertyMapper.toPropertyFloorResp(property),
        propertyMapper.propertyLandResp(property),
        detailsService.detailsResp(details, propertyImages, dataImages),
        propertyMapper.propertyAddressResp(property),
        sale != null ? saleService.saleResp(sale) : null,
        propertyMapper.propertyTemplateResp(property)
    );
  }

  @Transactional
  public void restore(Long id) {
    Property property = propertyService.getDeletedById(id);
    property.setDeletedAt(null);
  }

  @Transactional
  public void remove(Long id) {
    Property property = propertyService.getDeletedById(id);
    List<PropertyMember> pms = propertyMemberService.getByProperty(property.getId());
    propertyMemberService.deleteAll(pms);
    secretService.deleteByProperty(property.getId());
    taskNoteService.deleteByProperty(property.getId());
    propertyWorkLogService.deleteByProperty(property.getId());
    property.setDelete_persistent_at(LocalDateTime.now());
  }

  // ***************************** Deleted Property *****************************

  private List<Property> getAllPropertiesWithPaging() {
    long startTime = System.currentTimeMillis();
    List<Property> allProperties = new ArrayList<>();
    int batchSize = 1000;
    int page = 0;

    log.info("Starting paging query with batch size: {}", batchSize);

    while (true) {
      long batchStart = System.currentTimeMillis();
      Pageable pageable = PageRequest.of(page, batchSize);
      Page<Property> propertyPage = propertyRepository.findAllWithBasicDetails(pageable);
      long batchEnd = System.currentTimeMillis();

      allProperties.addAll(propertyPage.getContent());

      log.info("Batch {} completed: {}ms, Records: {}, Total: {}",
          page, batchEnd - batchStart, propertyPage.getContent().size(), allProperties.size());

      if (!propertyPage.hasNext()) {
        break;
      }
      page++;

      // 메모리 상태 로그 (10배치마다)
      if (page % 10 == 0) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        log.info("Memory status at batch {}: Used {}MB, Free {}MB",
            page, usedMemory / 1024 / 1024, runtime.freeMemory() / 1024 / 1024);
      }
    }

    long totalTime = System.currentTimeMillis() - startTime;
    log.info("Paging query completed: Total {}ms, Batches: {}, Records: {}",
        totalTime, page + 1, allProperties.size());

    return allProperties;
  }


  public ByteArrayInputStream createDeletedExcelFile(Long adminId, List<Long> ids) {
    {
      long startTime = System.currentTimeMillis();
      log.info("=== Excel Generation Started === AdminId: {}, IdsCount: {}",
          adminId, ids != null ? ids.size() : "ALL");

      List<Property> properties;
      if (ids != null && !ids.isEmpty()) {
        long queryStart = System.currentTimeMillis();
        properties = propertyRepository.findDeletedByIdInWithAllDetails(ids).stream()
            .sorted(Comparator.comparing(Property::getId))
            .toList();
        long queryEnd = System.currentTimeMillis();
        log.info("Specific IDs Query completed: {}ms, Records: {}",
            queryEnd - queryStart, properties.size());
      } else {
        // 페이징 배치 처리로 전체 데이터 조회
        long queryStart = System.currentTimeMillis();
        properties = getAllDeletedPropertiesWithPaging();
        long queryEnd = System.currentTimeMillis();
        log.info("Paging Query completed: {}ms, Records: {}",
            queryEnd - queryStart, properties.size());
      }

      final String[] PROPERTY_HEADERS = {
          "매물번호", "공개여부", "진행상태", "담당자명", "건물명", "대분류", "소분류", "거래유형",
          "등록일", "수정일", "시/도", "구/군", "동/읍/면", "상세주소",

          // price Info
          "매매가(만원)", "입금가(만원)", "기타매매가(만원)", "수익률(%)", "평단가(만원)",
          "보증금(만원)", "월임대료(만원)", "관리비(만원)", "관리비 지출(만원)", "관리비 기타(만원)", "대출현황",

          "상세정보", "비밀메모",

          // 토지 이용 계획
          // 지목 / 용도 지역
          "기본토지정보", "기본토지용도",
          // ledge
          "지하층", "지상층",
          "대지면적(㎡)", "대지면적(평)", "연면적(㎡)", "연면적(평)", "건축면적(㎡)", "건축면적(평)",
          "건폐율", "용적률", "주용도", "주구조", "건물 높이", "지붕구조",
          "주차(자주식,옥내)", "주차(자주식,옥외)", "주차(기계식,옥내)", "주차(기계식,옥외)", "주차(전치가,옥내)", "주차(전치가,옥외)",
          "엘리베이터(일반)", "엘리베이터(화물)",
          "세대수", "가구수",
          "허가일", "착공일", "사용승인일", "리모델링일",

          // 고객 정보 (고객1~10)
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

        // 헤더 생성
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
            .collect(Collectors.toMap(
                    Admin::getId,
                    Function.identity()
                )
            );

        List<PnuTable> pnus = pnuTableService.gets();
        Map<String, String> sidoCache = pnus.stream()
            .filter(pnu -> pnu.getType() == PnuTableType.SIDO)
            .collect(Collectors.toMap(
                PnuTable::getSido,
                PnuTable::getSidoName,
                (existing, replacement) -> existing // 중복 시 기존 값 유지
            ));;
        Map<String, String> sigunguCache = pnus.stream()
            .filter(pnu -> pnu.getType() == PnuTableType.SIGUNGU)
            .collect(Collectors.toMap(
                pnu -> pnu.getSido() + pnu.getSigungu(),
                PnuTable::getSigunguName,
                (existing, replacement) -> existing
            ));;
        Map<String, String> bjdongCache = pnus.stream()
            .filter(pnu -> pnu.getType() == PnuTableType.BJDONG)
            .collect(Collectors.toMap(
                pnu -> pnu.getSido() + pnu.getSigungu() + pnu.getBjd(),
                PnuTable::getBjdName,
                (existing, replacement) -> existing
            ));


        Map<Long, Details> detailsMap = detailsService.getsByPropertyId(propertyIds).stream()
            .distinct()
            .collect(
                Collectors.toMap(
                    dt -> dt.getProperty().getId(),
                    Function.identity()
                )
            );
        Map<Long, List<Secret>> secretsMap = secretService.getsByPropertyIds(propertyIds)
            .stream()
            .collect(
                Collectors.groupingBy(secret -> secret.getProperty().getId())
            );

        Map<Long, List<PropertyMember>> pmMap = propertyMemberService.getsByPropertyIds(propertyIds).stream()
            .collect(
                Collectors.groupingBy(PropertyMember::getPropertyId)
            );


        Set<Long> memberIds = pmMap.values().stream()
            .flatMap(List::stream)
            .map(PropertyMember::getMemberId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, Member> memberMap = memberService.gets(memberIds)
            .stream()
            .collect(
                Collectors.toMap(
                    Member::getId,
                    Function.identity()
                )
            );

        Map<Long, List<MemberNote>> memberNoteMap = memberNoteService.getsByMemberIn(memberIds).stream()
            .collect(
                Collectors.groupingBy(mn -> mn.getMember().getId())
            );

        long dataStart = System.currentTimeMillis();
        for (int i = 0; i < properties.size(); i++) {

          Property property = properties.get(i);

          int rowNum = i + 1;
          Row row = sheet.createRow(rowNum);

          // 1000행마다 진행 상황 로그
          if (i > 0 && i % 1000 == 0) {
            long currentTime = System.currentTimeMillis();
            log.info("Excel row processing: {}/{} completed, {}ms elapsed",
                i, properties.size(), currentTime - dataStart);

            // 메모리 상태 체크
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            log.info("Memory usage during Excel generation: {}MB", usedMemory / 1024 / 1024);
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
                    pnu.getSigunguCd().substring(2, 5), pnu.getBjdongCd(),bjdongCache));
          }
          setCellValueSafely(row, cellNum++, address::getRoadAddress);

          // price Info
          PriceProperties price = property.getPrice().getProperties();
          if (price == null) {
            price = new PriceProperties();
          }
          setCellValueSafely(row, cellNum++, price::getMmPrice);
          setCellValueSafely(row, cellNum++, price::getIgPrice);
          setCellValueSafely(row, cellNum++, price::getEtcPrice);
          setCellValueSafely(row, cellNum++, price::getRoi);
          setCellValueSafely(row, cellNum++, price::getPyeongPrice);
          setCellValueSafely(row, cellNum++, price::getDepositPrice);
          setCellValueSafely(row, cellNum++, price::getMonthPrice);
          setCellValueSafely(row, cellNum++, price::getGrPrice);
          setCellValueSafely(row, cellNum++, price::getGrOut);
          setCellValueSafely(row, cellNum++, price::getGrEtc);
          setCellValueSafely(row, cellNum++, price::getLoanDescription);

          // 상세정보
          Details detail = detailsMap.get(property.getId());
          setCellValueSafely(row, cellNum++, detail::getContent);

          // 비밀 메모
          StringBuilder secretStr = new StringBuilder();
          List<Secret> secrets = secretsMap.get(property.getId());

          if (secrets == null) {
            secrets = List.of();
          }

          Admin admin = adminMap.get(adminId);

          if (admin.getRole() !=null && admin.getRole().equals(AdminRole.ADMIN)) {
            for (Secret secret : secrets) {
              secretStr.append(getMultipleLineStr(secret::getContent));
            }
          } else {
            Department department = admin.getDepartment();
            Permission permission = admin.getPermission();
            SecretMemoExposeLevel level =
                permission != null ? permission.getSecretMemoExposeLevel() : null;
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
          StringBuilder minFloorStr = new StringBuilder(); // 지하층
          StringBuilder maxFloorStr = new StringBuilder(); // 지상층

          StringBuilder landAreaMeter = new StringBuilder(); // 대지면적(㎡)
          StringBuilder landAreaPyeong = new StringBuilder(); // 대지면적(평)
          StringBuilder yeonAreaMeter = new StringBuilder(); // 연면적(㎡)
          StringBuilder yeonAreaPyeong = new StringBuilder(); // 연면적(평)
          StringBuilder buildingAreaMeter = new StringBuilder(); // 건축면적(㎡)
          StringBuilder buildingAreaPyeong = new StringBuilder(); // 건축면적(평)

          StringBuilder geonPye = new StringBuilder(); // 건폐
          StringBuilder yongjeok = new StringBuilder(); // 용적
          StringBuilder mainPurpsCdNm = new StringBuilder(); // 주용도
          StringBuilder structure = new StringBuilder(); // 주구조
          StringBuilder heights = new StringBuilder(); // 높이
          StringBuilder jiboong = new StringBuilder(); // 지붕 구조

          StringBuilder jajuInParking = new StringBuilder(); // 자주식 옥내
          StringBuilder jajuOutParking = new StringBuilder(); // 자주식 옥외
          StringBuilder kigyeInParking = new StringBuilder(); // 기계식(옥내) 주차대수
          StringBuilder kigyeOutParking = new StringBuilder(); // 기계식(옥외) 주차대수
          StringBuilder elcInParking = new StringBuilder(); // 전기차(옥내) 주차대수
          StringBuilder elcOutParking = new StringBuilder(); // 전기차(옥외) 주차대수

          StringBuilder ilbanElevator = new StringBuilder(); // 일반승강기 수
          StringBuilder hwamoolElevator = new StringBuilder(); // 화물승강기 수

          StringBuilder saedae = new StringBuilder(); // 세대수
          StringBuilder gagu = new StringBuilder(); // 가구수

          StringBuilder heogaeDate = new StringBuilder(); // 허가일자
          StringBuilder chakGongDate = new StringBuilder(); // 착공일자
          StringBuilder sengInDate = new StringBuilder(); // 사용승인일자
          StringBuilder remodelDate = new StringBuilder(); // 리모델링일자

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

          // 고객 정보
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
            ;

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

        // 엑셀 파일 생성
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

  }

  private List<Property> getAllDeletedPropertiesWithPaging() {
    List<Property> allProperties = new ArrayList<>();
    int batchSize = 1000;
    int page = 0;

    while (true) {
      Pageable pageable = PageRequest.of(page, batchSize);
      Page<Property> propertyPage = propertyRepository.findDeletedAllWithBasicDetails(pageable);

      if (propertyPage.isEmpty()) {
        break;
      }

      allProperties.addAll(propertyPage.getContent());
      page++;

      if (!propertyPage.hasNext()) {
        break;
      }
    }

    return allProperties;
  }

  public String getSidoNameFromCache(String sidoCode, Map<String,String> sidoCache) {
    return sidoCode != null ? sidoCache.get(sidoCode) : null;
  }

  public String getSigunguNameFromCache(String sidoCode, String sigunguCode, Map<String,String> sigunguCache) {
    if (sidoCode == null || sigunguCode == null) return null;
    return sigunguCache.get(sidoCode + sigunguCode);
  }

  public String getBjdongNameFromCache(String sidoCode, String sigunguCode, String bjdongCode, Map<String,String> bjdongCache) {
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

  public ByteArrayInputStream createCsvFile(Long adminId, List<Long> ids) {
    List<Property> properties;
    if (ids != null && !ids.isEmpty()) {
      properties = propertyRepository.findByIdInWithAllDetails(ids).stream()
          .sorted(Comparator.comparing(Property::getId))
          .toList();
    } else {
      properties = getAllPropertiesWithPaging();
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

    try {
      StringBuilder csv = new StringBuilder();

      // 헤더 추가
      csv.append(String.join(",", PROPERTY_HEADERS)).append("\n");

      for (Property property : properties) {
        List<String> row = new ArrayList<>();

        // 기존 setCellValueSafely 로직을 CSV용으로 변환 (null 안전 처리)
        row.add(csvValue(property.getId()));
        row.add(csvValue(property.getIsPublic() != null ? (property.getIsPublic() ? "공개" : "비공개") : ""));
        row.add(csvValue(property.getStatus() != null ? property.getStatus().getDisplayName() : ""));

        // 담당자명
        try {
          Long aId = property.getAdminId();
          if (aId != null) {
            Admin admin = adminService.getById(aId);
            row.add(csvValue(admin != null ? admin.getName() : ""));
          } else {
            row.add("");
          }
        } catch (Exception e) {
          row.add("");
        }

        row.add(csvValue(property.getBuildingName()));
        row.add(csvValue(property.getBigCategory() != null ? property.getBigCategory().getName() : ""));

        String smallCategories = "";
        if (property.getSmallCategories() != null && !property.getSmallCategories().isEmpty()) {
          smallCategories = property.getSmallCategories().stream()
              .filter(category -> category != null && category.getName() != null)
              .map(category -> category.getName())
              .collect(Collectors.joining("; "));
        }
        row.add(csvValue(smallCategories));
        row.add(csvValue("매매"));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        row.add(csvValue(property.getCreatedAt() != null ? dateTimeFormatter.format(property.getCreatedAt()) : ""));
        row.add(csvValue(property.getUpdatedAt() != null ? dateTimeFormatter.format(property.getUpdatedAt()) : ""));

        // 주소 정보 (null 안전 처리)
        try {
          AddressProperties address = property.getAddress() != null ? property.getAddress().getProperties() : null;
          if (address != null && address.getPnu() != null) {
            PnuComponents pnu = PnuComponents.parse(address.getPnu());
            row.add(csvValue(pnuTableService.getSidoName(pnu.getSigunguCd().substring(0, 2))));
            row.add(csvValue(pnuTableService.getSigunguName(pnu.getSigunguCd().substring(0, 2), pnu.getSigunguCd().substring(2, 5))));
            row.add(csvValue(pnuTableService.getBjdongName(pnu.getSigunguCd().substring(0, 2), pnu.getSigunguCd().substring(2, 5), pnu.getBjdongCd())));
          } else {
            row.add("");
            row.add("");
            row.add("");
          }
          row.add(csvValue(address != null ? address.getRoadAddress() : ""));
        } catch (Exception e) {
          row.add("");
          row.add("");
          row.add("");
          row.add("");
        }

        // 가격 정보 (null 안전 처리)
        try {
          PriceProperties price = property.getPrice() != null ? property.getPrice().getProperties() : null;
          row.add(csvValue(price != null ? price.getMmPrice() : null));
          row.add(csvValue(price != null ? price.getIgPrice() : null));
          row.add(csvValue(price != null ? price.getEtcPrice() : null));
          row.add(csvValue(price != null ? price.getRoi() : null));
          row.add(csvValue(price != null ? price.getPyeongPrice() : null));
          row.add(csvValue(price != null ? price.getDepositPrice() : null));
          row.add(csvValue(price != null ? price.getMonthPrice() : null));
          row.add(csvValue(price != null ? price.getGrPrice() : null));
          row.add(csvValue(price != null ? price.getGrOut() : null));
          row.add(csvValue(price != null ? price.getGrEtc() : null));
          row.add(csvValue(price != null ? price.getLoanDescription() : null));
        } catch (Exception e) {
          // 가격 정보 에러 시 빈 값으로 채우기
          for (int i = 0; i < 11; i++) {
            row.add("");
          }
        }

        // 상세정보 (null 안전 처리)
        try {
          Details detail = detailsService.getByPropertyId(property.getId());
          row.add(csvValue(detail != null ? detail.getContent() : ""));
        } catch (Exception e) {
          row.add("");
        }

        // 비밀 메모 (간단히 처리)
        row.add("");

        // 토지 정보 (간단히 처리)
        row.add("");
        row.add("");

        // 건축물 정보 (간단히 처리 - 22개 필드)
        for (int i = 0; i < 22; i++) {
          row.add("");
        }

        // 고객 정보 (간단히 처리 - 60개 필드)
        for (int i = 0; i < 60; i++) {
          row.add("");
        }

        csv.append(String.join(",", row)).append("\n");
      }

      return new ByteArrayInputStream(csv.toString().getBytes("UTF-8"));

    } catch (Exception e) {
      throw new BusinessException(ErrorCode.ERROR_WHITE_PROCESS_XLSX, e.getMessage());
    }
  }

  // 초고속 CSV 생성 (핵심 데이터만)
  private String getValSafely(Supplier<Object> supplier) {
    try {
      return String.valueOf(supplier.get());
    } catch (Exception e) {
      return "";
    }
  }
  private String csvValue(Object value) {
    if (value == null) return "";
    String str = String.valueOf(value);
    if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
      str = str.replace("\"", "\"\"");
      return "\"" + str + "\"";
    }
    return str;
  }

  private String getFinal(String string) {
    final String result = string;
    return result;
  }

  private String getMultipleLineStr(Supplier<Object> supplier) {
    return this.getValSafely(supplier) + "\n";
  }

}
