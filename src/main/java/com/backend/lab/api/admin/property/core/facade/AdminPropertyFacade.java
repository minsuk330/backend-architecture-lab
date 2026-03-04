package com.backend.lab.api.admin.property.core.facade;

import com.backend.lab.api.admin.property.core.usecase.CreatePropertyUseCase;
import com.backend.lab.api.admin.property.core.usecase.UpdatePropertyUseCase;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import com.backend.lab.api.admin.property.core.dto.req.PropertyAddressReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyDefaultReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyFloorReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq.FilterOptions;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatusUpdateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyTemplateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDefaultResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDetailResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLandResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLedgeResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPriceResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyFloorInfoItem;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyLandInfoResp;
import com.backend.lab.api.admin.property.info.facade.PropertyInfoFacade;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.openapi.service.gong.FloorApiService;
import com.backend.lab.common.openapi.service.kakao.KakaoApiService;
import com.backend.lab.common.openapi.service.kakao.dto.KakaoAddressSearchResponse;
import com.backend.lab.common.openapi.service.kakao.dto.KakaoAddressSearchResponse.KakaoDocument;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.entity.dto.req.DetailReq;
import com.backend.lab.domain.details.repository.DetailsRepository;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.memberNote.entity.MemberNote;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import com.backend.lab.domain.notification.entity.vo.NotificationType;
import com.backend.lab.domain.notification.service.NotificationService;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.service.CategoryService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
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
import com.backend.lab.domain.property.core.service.info.AddressInfoService;
import com.backend.lab.domain.property.core.service.info.FloorInfoService;
import com.backend.lab.domain.property.core.service.info.LandInfoService;
import com.backend.lab.domain.property.core.service.info.LedgeInfoService;
import com.backend.lab.domain.property.core.service.info.PriceInfoService;
import com.backend.lab.domain.property.core.service.info.RegisterInfoService;
import com.backend.lab.domain.property.pnuTable.entity.PnuTable;
import com.backend.lab.domain.property.pnuTable.entity.vo.PnuTableType;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyChangeDetectService;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyWorkLogService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.property.taskNote.repository.TaskNoteRepository;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.service.SecretService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminPropertyFacade {

  private final RegisterInfoRepository registerInfoRepository;
  private final TemplateFacade templateFacade;

  private final PropertyService propertyService;
  private final SecretService secretService;
  private final AdminService adminService;
  private final DetailsService detailsService;
  private final TaskNoteService taskNoteService;
  private final UploadFileService uploadFileService;

  private final AddressInfoService addressInfoService;
  private final PriceInfoService priceInfoService;
  private final RegisterInfoService registerInfoService;
  private final CategoryService categoryService;

  private final PropertyMapper propertyMapper;
  private final MemberService memberService;
  private final SaleService saleService;
  private final PnuTableService pnuTableService;
  private final PropertyMemberService propertyMemberService;
  private final MemberNoteService memberNoteService;
  private final CustomerService customerService;
  private final PriceInfoRepository priceInfoRepository;
  private final LedgeInfoRepository ledgeInfoRepository;
  private final LandInfoRepository landInfoRepository;
  private final AddressInfoRepository addressInfoRepository;
  private final TemplateInfoRepository templateInfoRepository;
  private final PropertyRepository propertyRepository;
  private final DetailsRepository detailsRepository;
  private final KakaoApiService kakaoApiService;
  private final FloorApiService floorApiService;
  private final FloorInfoRepository floorInfoRepository;
  private final PropertyInfoFacade propertyInfoFacade;
  private final PropertyAdvertisementService propertyAdvertisementService;
  private final TaskNoteRepository taskNoteRepository;
  private final PropertyWorkLogService propertyWorkLogService;
  private final CreatePropertyUseCase createPropertyUseCase;
  private final UpdatePropertyUseCase updatePropertyUseCase;

  @Transactionalㅇ
  public PropertyPublicResp updatePublicStatus(Long propertyId) {
    Boolean currentStatus = propertyService.togglePublic(propertyId);
    return PropertyPublicResp.builder()
        .isPublic(currentStatus)
        .build();
  }

  public void createProperty(PropertyCreateReq req, Long adminId, String clientIp) {
    createPropertyUseCase.execute(req, adminId, clientIp);
  }

  public void updateProperty(PropertyUpdateReq req, Long adminId, Long propertyId,
      String clientIp) {
    updatePropertyUseCase.execute(req, adminId, propertyId, clientIp);

  }

  public PageResp<PropertySearchResp> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    Page<Property> search = propertyService.search(options, adminIds, bigCategoryIds,
        smallCategoryIds);
    List<PropertySearchResp> list = search.stream().map(
        propertyMapper::propertySearchResp
    ).toList();

    return new PageResp<>(search, list);
  }


  public PropertyStatResp getStat(Long totalDepartmentId, Long totalMajorCategoryId,
      Long totalAdminId, Long monthlyAdminId, Long monthlyDepartmentId,
      Long monthlyMajorCategoryId) {

    PropertyStatReq req = new PropertyStatReq();
    if (totalDepartmentId != null || totalMajorCategoryId != null || totalAdminId != null) {
      FilterOptions totalFilter = new FilterOptions();
      totalFilter.setDepartmentId(totalDepartmentId);
      totalFilter.setMajorCategoryId(totalMajorCategoryId);
      totalFilter.setAdminId(totalAdminId);
      req.setTotal(totalFilter);

    }

    if (monthlyDepartmentId != null || monthlyMajorCategoryId != null || monthlyAdminId != null) {
      FilterOptions monthlyFilter = new FilterOptions();
      monthlyFilter.setDepartmentId(monthlyDepartmentId);
      monthlyFilter.setMajorCategoryId(monthlyMajorCategoryId);
      monthlyFilter.setAdminId(monthlyAdminId);
      req.setMonthly(monthlyFilter);
    }

    return propertyService.getStat(req);
  }

  public ListResp<PropertyListResp> getByMap(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    List<Property> list = propertyService.getsByMap(req, adminIds, bigCategoryIds,
        smallCategoryIds);
    List<PropertyListResp> data = list.stream().map(
        propertyMapper::propertyAdminMapListResp
    ).toList();

    return new ListResp<>(data);
  }


  public PageResp<PropertySearchResp> getsWithPropertyMember(Long memberId, PageOptions options) {
    Page<Property> page = propertyService.getsWithPropertyMember(memberId, options.pageable());
    List<PropertySearchResp> data = page.getContent().stream()
        .map(propertyMapper::propertySearchResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public PropertyStatusResp getStatus(Long propertyId) {
    Property property = propertyService.getById(propertyId);

    PropertyStatus status = property.getStatus();
    switch (status) {
      case READY -> {
        return PropertyStatusResp.builder().status(PropertyStatus.READY).build();
      }
      case COMPLETE -> {
        return PropertyStatusResp.builder().status(PropertyStatus.COMPLETE)
            .completedAt(property.getCompletedAt())
            .build();
      }
      case PENDING -> {
        return PropertyStatusResp.builder().status(PropertyStatus.PENDING)
            .pendingAt(property.getPendingAt())
            .build();
      }
      case SOLD -> {
        Sale sale = saleService.getByProperty(propertyId);
        if (sale != null) {
          SaleResp saleResp = saleService.saleResp(sale);
          PropertyStatusResp.builder()
              .status(PropertyStatus.SOLD)
              .sale(saleResp)
              .build();
        }
      }
    }
    return PropertyStatusResp.builder().status(PropertyStatus.READY).build();
  }

  @Transactional
  public PropertyStatusResp statusUpdate(PropertyStatusUpdateReq req, Long propertyId,
      Long userId) {
    PropertyStatus updateStatus = req.getUpdateStatus();
    Property property = propertyService.getById(propertyId);
    switch (updateStatus) {
      case READY -> {
        property.setStatus(updateStatus);
        return PropertyStatusResp.builder().status(PropertyStatus.READY).build();
      }
      case COMPLETE -> {
        property.setStatus(updateStatus);
        property.setCompletedAt(req.getCompletedAt());
        return PropertyStatusResp.builder().status(PropertyStatus.COMPLETE)
            .completedAt(req.getCompletedAt())
            .build();
      }
      case PENDING -> {
        property.setStatus(updateStatus);
        property.setPendingAt(req.getPendingAt());
        return PropertyStatusResp.builder().status(PropertyStatus.PENDING)
            .pendingAt(req.getPendingAt())
            .build();
      }
      case SOLD -> {
        property.setStatus(updateStatus);
        if (req.getSale() != null) {
          UploadFile contract = null;
          if (req.getSale().getContractId() != null) {
            contract = uploadFileService.getById(req.getSale().getContractId());
          }
          req.getSale().setContract(contract);
          saleService.create(req.getSale(), userId, propertyId);
          SaleResp saleResp = SaleResp.builder()
              .saleAt(req.getSale().getSaleAt())
              .memberId(userId)
              .earningPrice(req.getSale().getEarningPrice())
              .salePrice(req.getSale().getSalePrice())
              .propertyId(propertyId).build();

          return PropertyStatusResp.builder()
              .status(PropertyStatus.SOLD)
              .sale(saleResp)
              .build();
        }
      }
    }
    return PropertyStatusResp.builder().status(updateStatus).build();
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

  @Transactional
  public void assignPropertyAdvertisement(Long propertyId, Long agentId, LocalDateTime startDate,
      LocalDateTime endDate) {
    Property property = propertyService.getById(propertyId);
    Member agent = memberService.getById(agentId);
    propertyAdvertisementService.createAdvertisement(property, agent, startDate, endDate);
  }

  @Transactional
  public void removePropertyAdvertisement(Long propertyId, Long agentId) {
    propertyAdvertisementService.removeAdvertisement(propertyId, agentId);
  }


  @Transactional
  public Property mapToProperty(PropertyCreateReq req) {

    AddressInformation addressInformation = addressInfoService.create(
        req.getAddress() != null ? req.getAddress() : new PropertyAddressReq());

    Long yeonPyongPrice = calculateYeonPyongPrice(req.getPrice(), req.getLedges());

    PriceInformation priceInformation = priceInfoService.create(
        req.getPrice() != null ? req.getPrice() : new PriceProperties(), yeonPyongPrice, req.getLands());

    RegisterInformation registerInformation = registerInfoService.create(
        req.getRegister() != null ? req.getRegister() : new RegisterProperties());

    TemplateInformation templateInformation = templateFacade.createTemplateInformation(req);

    PropertyDefaultReq defaults = req.getDefaults();
    if (defaults != null && defaults.getThumbnailId() != null) {
      defaults.setThumbnailImageUrl(uploadFileService.getById(defaults.getThumbnailId()));
    } else {
      defaults.setThumbnailImageUrl(null);
    }
    Long exclusiveId = null;
    if (defaults.getExclusiveId() != null) {
      exclusiveId = defaults.getExclusiveId();

    }

    return Property.builder()
        .adminId(req.getDefaults().getAdminId())
        .isPublic(defaults.getIsPublic())
        .exclusiveAgentId(exclusiveId)
        .thumbnailImageUrl(defaults.getThumbnailImageUrl())
        .buildingName(defaults.getBuildingName())
        .status(defaults.getStatus())
        .bigCategory(categoryService.getById(defaults.getBigCategoryId()))
        .smallCategories(defaults.getSmallCategoryIds() != null ? 
            defaults.getSmallCategoryIds().stream()
                .map(categoryService::getById)
                .collect(Collectors.toSet()) : new LinkedHashSet<>())
        .address(addressInformation)
        .price(priceInformation)
        .register(registerInformation)
        .template(templateInformation)
        .exclusiveAgentId(exclusiveId)
        .build();
  }

  public PropertyDetailResp getInfo(Long propertyId, Long userId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);
    List<PropertyLedgeResp> propertyLedgeResps = propertyMapper.propertyLedgeResp(property);

    PropertyDefaultResp propertyDefaultResp = propertyMapper.propertyDefaultResp(property, null);

    List<PropertyLandResp> propertyLandResps = propertyMapper.propertyLandResp(property);

    PropertyPriceResp propertyPriceResp = propertyMapper.propertyPriceResp(property);

    return PropertyDetailResp.builder()
        .propertyLedge(propertyLedgeResps.get(0))
        .propertyDefault(propertyDefaultResp)
        .propertyLand(propertyLandResps.get(0))
        .propertyPrice(propertyPriceResp)
        .build();
  }

  public PropertyUpdateResp getUpdateProperty(Long adminId, Long propertyId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);
    Admin admin = adminService.getById(adminId);
    return propertyMapper.propertyUpdateResp(admin, property);
  }


  public PropertyResp get(Long propertyId, Long userId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);

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


  public ListResp<PropertyListResp> getList() {
    List<Property> list = propertyService.getList();

    List<PropertyListResp> data = list.stream().map(
        propertyMapper::propertyListResp
    ).toList();

    return new ListResp<>(data);
  }

  @Transactional
  public void delete(Long propertyId, Long userId, String clientIp) {
    Property property = propertyService.getById(propertyId);
    Admin admin = adminService.getById(userId);

    propertyWorkLogService.deleteLog(admin,property,clientIp);
    propertyService.deleteById(propertyId);
  }

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
      // 페이징 배치 처리로 전체 데이터 조회
      long queryStart = System.currentTimeMillis();
      properties = getAllPropertiesWithPaging();
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
  public ByteArrayInputStream createFastCsvFile(Long adminId, List<Long> ids) {
    List<Property> properties;
    if (ids != null && !ids.isEmpty()) {
      properties = propertyRepository.findByIdInWithAllDetails(ids).stream()
          .sorted(Comparator.comparing(Property::getId))
          .toList();
    } else {
      properties = getAllPropertiesWithPaging();
    }

    // 핵심 컬럼만 (추가 DB 조회 없음)
    final String[] HEADERS = {
        "매물번호", "공개여부", "진행상태", "건물명", "대분류", "거래유형",
        "등록일", "수정일", "상세주소", "매매가", "보증금", "월임대료"
    };

    try {
      StringBuilder csv = new StringBuilder();
      csv.append(String.join(",", HEADERS)).append("\n");

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      for (Property property : properties) {
        List<String> row = new ArrayList<>();

        // 기본 데이터만 (DB 추가 조회 없음)
        row.add(csvValue(property.getId()));
        row.add(csvValue(property.getIsPublic() != null ? (property.getIsPublic() ? "공개" : "비공개") : ""));
        row.add(csvValue(property.getStatus() != null ? property.getStatus().getDisplayName() : ""));
        row.add(csvValue(property.getBuildingName()));
        row.add(csvValue(property.getBigCategory() != null ? property.getBigCategory().getName() : ""));
        row.add(csvValue("매매"));
        row.add(csvValue(property.getCreatedAt() != null ? formatter.format(property.getCreatedAt()) : ""));
        row.add(csvValue(property.getUpdatedAt() != null ? formatter.format(property.getUpdatedAt()) : ""));

        // 주소 (간단히)
        try {
          AddressProperties address = property.getAddress() != null ? property.getAddress().getProperties() : null;
          row.add(csvValue(address != null ? address.getRoadAddress() : ""));
        } catch (Exception e) {
          row.add("");
        }

        // 가격 (핵심만)
        try {
          PriceProperties price = property.getPrice() != null ? property.getPrice().getProperties() : null;
          row.add(csvValue(price != null ? price.getMmPrice() : ""));
          row.add(csvValue(price != null ? price.getDepositPrice() : ""));
          row.add(csvValue(price != null ? price.getMonthPrice() : ""));
        } catch (Exception e) {
          row.add("");
          row.add("");
          row.add("");
        }

        csv.append(String.join(",", row)).append("\n");
      }

      return new ByteArrayInputStream(csv.toString().getBytes("UTF-8"));

    } catch (Exception e) {
      throw new BusinessException(ErrorCode.ERROR_WHITE_PROCESS_XLSX, e.getMessage());
    }
  }

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


  @Transactional
  public void confirm(Long propertyId) {
    Property property = propertyService.getById(propertyId);
    propertyService.confirm(property);
  }

  @Transactional
  public void removeConfirm(Long propertyId) {
    Property property = propertyService.getById(propertyId);
    propertyService.deleteConfirm(property);

  }

  private static final int BATCH_SIZE = 1000;

  public void taskNoteUploadFile(MultipartFile file) {
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      
      List<TaskNoteInsertData> batchInsertList = new ArrayList<>();
      int processedCount = 0;

      for (Row row : sheet) {
        if (row.getRowNum() == 0) {
          continue; // 헤더 행 스킵
        }

        try {
          TaskNoteInsertData insertData = parseRowToTaskNoteData(row);
          if (insertData != null) {
            batchInsertList.add(insertData);
            processedCount++;
          }
        } catch (Exception e) {
          log.error("행 {} 처리 실패: {}", row.getRowNum(), e.getMessage());
          continue;
        }

        if (batchInsertList.size() >= BATCH_SIZE) {
          processBatchInsertWithTransaction(batchInsertList);
          log.info("배치 처리 완료: {} 건", batchInsertList.size());
          batchInsertList.clear();
        }
      }

      // 남은 데이터 처리
      if (!batchInsertList.isEmpty()) {
        processBatchInsertWithTransaction(batchInsertList);
        log.info("최종 배치 처리 완료: {} 건", batchInsertList.size());
      }
      
      log.info("TaskNote 업로드 완료 - 총 처리: {} 건", processedCount);

    } catch (Exception e) {
      log.error("엑셀 업로드 실패", e);
      throw new RuntimeException("업로드 실패: " + e.getMessage());
    }
  }

  @Transactional
  public void processBatchInsertWithTransaction(List<TaskNoteInsertData> batchList) {
    processBatchInsert(batchList);
  }

  private void processBatchInsert(List<TaskNoteInsertData> batchList) {
    for (TaskNoteInsertData data : batchList) {
      try {
        if (data.taskNoteData != null) {
          taskNoteRepository.insertTaskNoteAuto(
            data.propertyId,
            data.taskType.name(),
            data.createdAt,
            data.taskNoteData.beforeValue,
            data.taskNoteData.afterValue,
            data.taskNoteData.logFieldType.ordinal(),
            data.adminId
          );
        } else {
          taskNoteRepository.insertTaskNote(
            data.propertyId,
            data.taskType.name(),
            data.createdAt,
            data.content,
            data.adminId
          );
        }
      } catch (Exception e) {
        log.error("TaskNote 저장 실패 - propertyId: {}, error: {}", data.propertyId, e.getMessage());
      }
    }
  }

  @Async
  @Transactional
  public void floorUpdate() {
    List<Property> properties = propertyService.gets();

    Map<String, Property> pnuToPropertyMap = properties.stream()
        .filter(property -> property.getAddress() != null
            && property.getAddress().getProperties() != null
            && property.getAddress().getProperties().getPnu() != null)
        .collect(Collectors.toMap(
            property -> property.getAddress().getProperties().getPnu(),
            Function.identity(),
            (existing, replacement) -> existing
        ));

    int totalCount = pnuToPropertyMap.size();
    AtomicInteger processedCount = new AtomicInteger(0);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    log.info("Floor 업데이트 시작 - 총 {}개 처리 예정", totalCount);

    pnuToPropertyMap.forEach((pnu, property) -> {
      int currentProcessed = processedCount.incrementAndGet();

      try {
        CompletableFuture<List<PropertyFloorInfoItem>> floorResponse = floorApiService.getFloorInfo(pnu);
        List<PropertyFloorInfoItem> floorInfo = floorResponse.get();

        if (floorInfo != null && !floorInfo.isEmpty()) {
          property.getFloors().forEach(floorInformation -> {
            if (floorInformation.getProperties() != null) {
              String propertyFloor = floorInformation.getProperties().getFloor();
              String upjong = floorInformation.getProperties().getUpjong();
              
              floorInfo.stream()
                  .filter(apiFloor -> Objects.equals(apiFloor.getFloor(), propertyFloor) && Objects.equals(apiFloor.getUpjong(), upjong))
                  .findFirst()
                  .ifPresent(matchedFloor -> {
                    Integer apiAreaPyung = matchedFloor.getAreaPyung() != null ? matchedFloor.getAreaPyung().intValue() : null;
                    Integer apiAreaMeter = matchedFloor.getAreaMeter() != null ? matchedFloor.getAreaMeter().intValue() : null;
                    Integer currentAreaPyung = floorInformation.getProperties().getAreaPyung() != null ? floorInformation.getProperties().getAreaPyung().intValue() : null;
                    Integer currentAreaMeter = floorInformation.getProperties().getAreaMeter() != null ? floorInformation.getProperties().getAreaMeter().intValue() : null;

                    // 기존 areaPyung == areaMeter
                    boolean currentValuesEqual = Objects.equals(currentAreaPyung, currentAreaMeter);
                    
                    //기존 값과 API 값이 같으면
                    boolean apiValuesMatch = Objects.equals(apiAreaPyung, currentAreaPyung) && Objects.equals(apiAreaMeter, currentAreaMeter);

                    boolean upJongMatch = Objects.equals(matchedFloor.getUpjong(), floorInformation.getProperties().getUpjong());

                    if (currentValuesEqual || (apiValuesMatch && upJongMatch)) {
                      floorInformation.getProperties().setAreaPyung(matchedFloor.getAreaPyung());
                      floorInformation.getProperties().setAreaMeter(matchedFloor.getAreaMeter());
                    }
                  });
            }
          });

          propertyRepository.save(property);
          successCount.incrementAndGet();
        }
      } catch (Exception e) {
        log.error("호출 실패 - PNU: {}, PropertyId: {}", pnu, property.getId());
      }

      if (currentProcessed % Math.max(1, totalCount / 10) == 0 || currentProcessed % 100 == 0) {
        double progressPercentage = (double) currentProcessed / totalCount * 100;
        log.info("Floor 업데이트 진행률: {}/{} ({}%) - 성공: {}, 실패: {}", currentProcessed, totalCount, progressPercentage, successCount.get(), errorCount.get());
      }
    });
    log.info("#### 완 ####");
  }




  private static class TaskNoteInsertData {
    public final Long propertyId;
    public final TaskType taskType;
    public final LocalDateTime createdAt;
    public final String content;
    public final Long adminId;
    public final TaskNoteData taskNoteData;

    public TaskNoteInsertData(Long propertyId, TaskType taskType, LocalDateTime createdAt, 
                              String content, Long adminId, TaskNoteData taskNoteData) {
      this.propertyId = propertyId;
      this.taskType = taskType;
      this.createdAt = createdAt;
      this.content = content;
      this.adminId = adminId;
      this.taskNoteData = taskNoteData;
    }
  }

  public static class TaskNoteData {
    public final LogFieldType logFieldType;
    public final String beforeValue;
    public final String afterValue;

    public TaskNoteData(LogFieldType logFieldType, String beforeValue, String afterValue) {
      this.logFieldType = logFieldType;
      this.beforeValue = beforeValue;
      this.afterValue = afterValue;
    }
  }

  private LogFieldType mapKoreanToLogFieldType(String korean) {
    return switch (korean) {
      case "고객휴대전화" -> LogFieldType.PHONE_NUMBER;
      case "진행상태" -> LogFieldType.STATUS;
      case "수익률" -> LogFieldType.ROI;
      case "담당자" -> LogFieldType.ADMIN;
      case "월임대료" -> LogFieldType.MONTH_PRICE;
      case "보증금" -> LogFieldType.DEPOSIT_PRICE;
      case "평단가" -> LogFieldType.PYENG_PRICE;
      case "매매가" -> LogFieldType.MM_PRICE;
      default -> null;
    };
  }

  public TaskNoteData parseTaskNoteContent(String content) {
    if (content == null || content.trim().isEmpty()) {
      return null;
    }

    // 매매가 : 150억 ▶ 142억 으로 변경되었습니다.
    String pattern = "^(.+?)\\s*:\\s*(.+?)\\s*▶\\s*(.+?)\\s*으로";
    
    java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
    java.util.regex.Matcher matcher = regex.matcher(content.trim());
    
    if (matcher.find()) {
      String koreanFieldType = matcher.group(1).trim();
      String beforeValue = matcher.group(2).trim();
      String afterValue = matcher.group(3).trim();
      
      LogFieldType logFieldType = mapKoreanToLogFieldType(koreanFieldType);
      if (logFieldType != null) {
        return new TaskNoteData(logFieldType, beforeValue, afterValue);
      }
    }
    
    return null;
  }

  @Transactional
  public void uploadExcelFile(MultipartFile file) {
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);

      for (Row row : sheet) {
        if (row.getRowNum() == 0) {
          continue; // 헤더 행 스킵
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

  private TaskNoteInsertData parseRowToTaskNoteData(Row row) {
    String propertyIdStr = getCellValue(row, 1);

    String type = getCellValue(row, 5);
    String content = getCellValue(row, 6);
    String createdAtStr = getCellValue(row, 7);

    LocalDateTime createdAt = parseDateTime(createdAtStr);

    if (propertyIdStr.isEmpty()) {
      log.warn("필수 정보 누락으로 행 건너뜀 - 매물번호: {}", propertyIdStr);
      return null;
    }

    Long propertyId = Long.parseLong(propertyIdStr);

    if (!propertyService.getByexist(propertyId)) {
      log.warn("존재하지 않는 매물번호로 행 건너뜀 - 매물번호: {}", propertyId);
      return null;
    }

    TaskType taskType = convertToTaskType(type);
    Long adminId = 2L;

    if (taskType == TaskType.AUTO_UPDATE) {
      TaskNoteData taskNoteData = parseTaskNoteContent(content);
      return new TaskNoteInsertData(propertyId, taskType, createdAt, content, adminId, taskNoteData);
    }

    return new TaskNoteInsertData(propertyId, taskType, createdAt, content, adminId, null);
  }


  private TaskType convertToTaskType(String statusStr) {
    if (statusStr == null || statusStr.isEmpty()) {
      return null;
    }

    switch (statusStr) {
      case "미팅":
        return TaskType.MEETING;
      case "변경":
        return TaskType.AUTO_UPDATE;
      case "계약":
        return TaskType.CONTRACT;
      case "기타":
        return TaskType.ETC;
      case "임장":
        return TaskType.IMJANG;
      case "전화":
        return TaskType.CALL;
      default:
        log.warn("알 수 없는 상태값: {}", statusStr);
        return null;

    }

  }

  private Property parseRowToProperty(Row row) {
    try {
      // 기본 정보 추출
      String propertyIdStr = getCellValue(row, 0);  // 매물번호
      String isPublicStr = getCellValue(row, 2);    // 공개여부
      String statusStr = getCellValue(row, 3);      // 진행상태
      String buildingName = getCellValue(row, 5);   // 건물명
      String bigCategoryName = getCellValue(row, 6); // 대분류
      String smallCategoryName = getCellValue(row, 7); // 소분류

      LocalDateTime createdAt = parseDateTime(getCellValue(row, 11));
      LocalDateTime updatedAt = parseDateTime(getCellValue(row, 12));



      if (buildingName.equals("-")) {
        buildingName = "";
      }

      // 필수 값 검증
      if (propertyIdStr.isEmpty()) {
        log.warn("필수 정보 누락으로 행 건너뜀 - 매물번호: {}", propertyIdStr);
        return null;
      }
      // 외래키 조회
      Category bigCategory = null;
      Set<Category> smallCategories = new LinkedHashSet<>();
      Admin admin = adminService.getByEmail("admin@test.com");
      Long adminId = admin.getId();

      try {
        // 대분류 처리
        if (!bigCategoryName.isEmpty() && !bigCategoryName.equals("-")) {
          String firstBigCategory = bigCategoryName.split(",")[0].trim();
          if (!firstBigCategory.isEmpty()) {
            bigCategory = categoryService.findByName(firstBigCategory);
          }
        }

//         소분류 처리 (여러개)
        if (!smallCategoryName.isEmpty() && !smallCategoryName.equals("-")) {
          String[] categoryNames = smallCategoryName.split(",");
          for (String categoryName : categoryNames) {
            String trimmedName = categoryName.trim();
            if (!trimmedName.isEmpty()) {
              Category category = categoryService.findByName(trimmedName);
              if (category != null) {
                smallCategories.add(category);
                log.debug("소분류 추가됨: {}", trimmedName);
              } else {
                log.warn("소분류를 찾을 수 없음: {}", trimmedName);
              }
            }
          }
        }
      } catch (Exception e) {
        log.warn("참조 데이터 조회 실패 - 매물번호: {}, 대분류: {}, 소분류: {}, 오류: {}",
            buildingName, bigCategoryName, smallCategoryName, e.getMessage());
      }

      // PropertyStatus enum 변환
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
        try{
          PropertyLandInfoResp landInfoApiResp = propertyInfoFacade.getLandInfo(pnu);
          LandProperties land = landInfoApiResp.getLand();
          landInfo.setProperties(land);
        }
        catch (Exception e) {
          log.warn("토지정보 조회 실패 - PNU: {}, 오류: {}", pnu, e.getMessage());
        }

      }
      landInfo = landInfoRepository.save(landInfo);
      property.getLand().add(landInfo);

      if (pnu != null) {
        // 층별 정보 주입
        try {
          List<PropertyFloorInfoItem> floorItems = floorApiService.getFloorInfo(pnu).get();
          List<PropertyFloorInfoItem> list = floorItems.stream()
              .sorted(Comparator.nullsLast(Comparator.comparing(PropertyFloorInfoItem::getFloor)))
              .toList();
          for (int i = 0; i < list.size(); i++) {
            PropertyFloorInfoItem item = list.get(i);
            FloorInformation floorInfo = FloorInformation.builder()
                .properties(
                    FloorProperties.builder()
                        .floor(item.getFloor())
                        .areaMeter( Math.floor(item.getAreaMeter()))
                        .areaPyung( Math.floor(item.getAreaPyung()))
                        .upjong(item.getUpjong())
                        .build()
                )
                .buildingOrder(0L)
                .rank((long) i)
                .buildingOrder(0L)
                .isPublic(true)
                .build();

            floorInfoRepository.save(floorInfo);
            property.getFloors().add(floorInfo);
          }
        } catch (Exception e) {
          log.warn("층별 정보 처리 실패 - 매물번호: {}, PNU: {}, 오류: {}", property.getId(), pnu, e.getMessage());
        }
      }

      // Details 생성 및 저장
      String detailContent = getCellValue(row, 41);  // 상세정보 컬럼

    if (detailContent.equals("-")) {
        detailContent = null;
    }

    Details details = Details.builder()
        .content(detailContent)
        .build();

    details.setProperty(property);
    detailsRepository.save(details);
      // 고객 정보 처리
      createMembers(row, property, adminId);

      try {
        if (!smallCategories.isEmpty()) {
          property.getSmallCategories().addAll(smallCategories);
          log.debug("Property {}에 소분류 {} 개 설정됨", property.getId(), smallCategories.size());
        } else {
          log.debug("Property {}에 설정할 소분류 없음 - 원본: {}", property.getId(), smallCategoryName);
        }
      } catch (Exception e) {
        log.warn("소분류 설정 실패 - 매물번호: {}, 소분류: {}, 오류: {}", property.getId(), smallCategoryName, e.getMessage());
      }

      // Property 최종 업데이트 (모든 연관관계 설정 후)
      property = propertyRepository.save(property);

      // 엑셀의 원본 날짜로 재설정
      propertyRepository.updatePropertyDates(property.getId(), createdAt, updatedAt);

      return property;

    } catch (Exception e) {
      log.error("행 파싱 중 오류 발생 - 행번호: {}, 오류: {}", row.getRowNum(), e.getMessage(), e);
      return null;
    }
  }



  private void createMembers(Row row, Property property, Long adminId) {
    // 고객1~4까지 처리
    for (int i = 0; i < 4; i++) {
      MemberInfo memberInfo = extractMemberInfo(row, i);
      if (memberInfo != null && memberInfo.hasValidData()) {
        Member customer = customerService.createCustomer(memberInfo, adminId);
        propertyMemberService.create(customer.getId(), property.getId());
        log.info("고객 정보 추출됨 - 고객{}: {}", (i + 1), memberInfo);
      }
    }
  }

  private AddressInformation createAddressInformation(Row row, Property property) {
    String sido = getCellValue(row, 13);     // 시/도
    String gugun = getCellValue(row, 14);    // 구/군
    String dong = getCellValue(row, 15);     // 동/읍/면
    String detail = getCellValue(row, 16);   // 상세주소
    detail = extractAddressNumber(detail);  // 괄호/쉼표 앞까지만 추출

    // 주소가 없으면 null 반환
    if (sido.isEmpty() && gugun.isEmpty() && dong.isEmpty() && detail.isEmpty()) {
      return AddressInformation.builder()
          .properties(AddressProperties.builder().build())
          .build();
    }

    // 지번주소 조합
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

        // 산 여부에 따른 구분값
        String mountainNumber = "N".equals(document.getAddress().getMountainYn()) ? "1" : "2";

        // mainAddressNo와 subAddressNo를 4자리로 패딩
        String mainAddressNo = String.format("%04d",
            Integer.parseInt(document.getAddress().getMainAddressNo()));

        String subAddressNo = String.format("%04d",
            Integer.parseInt(
                document.getAddress().getSubAddressNo() != null && !document.getAddress()
                    .getSubAddressNo().isEmpty() ?
                    document.getAddress().getSubAddressNo() : "0"));

        // PNU 생성: bCode + 산구분 + mainAddressNo(4자리) + subAddressNo(4자리)
        pnu = document.getAddress().getBCode() + mountainNumber + mainAddressNo + subAddressNo;
        log.info("================================================");
        log.info("pnu:{}", pnu);
        // 좌표 정보
        lat = Double.parseDouble(document.getY());
        lng = Double.parseDouble(document.getX());

        PnuComponents pnuComponents = PnuComponents.parse(pnu);
        bun = Integer.valueOf(pnuComponents.getBun());
        ji = Integer.valueOf(pnuComponents.getJi());


        // 도로명주소
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
    String mmPriceStr = getCellValue(row, 19);      // 매매가(만원)
    String igPriceStr = getCellValue(row, 20);      // 입금가(만원)
    String etcPriceStr = getCellValue(row, 21);     // 기타매매가(만원)
    String roiStr = getCellValue(row, 22);          // 수익률(%)
    String pyeongPriceStr = getCellValue(row, 23);  // 평단가(만원)
    String depositStr = getCellValue(row, 24);      // 보증금(만원)
    String monthlyRentStr = getCellValue(row, 25);  // 월임대료(만원)
    String managementFeeStr = getCellValue(row, 26); // 관리비(만원)
    String grOutStr = getCellValue(row, 27);        // 관리비 지출(만원)
    String grEtcStr = getCellValue(row, 28);        // 관리비 기타(만원)
    String loanStr = getCellValue(row, 29);         // 대출현황

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

    String landAreaPyeong = getCellValue(row, 31);   // 대지면적(평)
    String yeonAreaPyeong = getCellValue(row, 33);   // 연면적(평)

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

    // 가격 정보가 하나도 없으면 null 반환
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
    String undergroundStr = getCellValue(row, 17);   // 지하층
    String abovegroundStr = getCellValue(row, 18);   // 지상층
    String landAreaMeter = getCellValue(row, 30);    // 대지면적(㎡)
    String landAreaPyeong = getCellValue(row, 31);   // 대지면적(평)
    String yeonAreaMeter = getCellValue(row, 32);    // 연면적(㎡)
    String yeonAreaPyeong = getCellValue(row, 33);   // 연면적(평)
    String buildingAreaMeter = getCellValue(row, 34); // 건축면적(㎡)
    String buildingAreaPyeong = getCellValue(row, 35); // 건축면적(평)
    String geonPyeStr = getCellValue(row, 47);       // 건폐율
    String yongjeokStr = getCellValue(row, 48);      // 용적률
    String structureStr = getCellValue(row, 50);     // 주구조
    String heightStr = getCellValue(row, 51);        // 건물 높이
    String jiboongStr = getCellValue(row, 52);       // 지붕구조
    String jajuInStr = getCellValue(row, 53);        // 주차(자주식,옥내)
    String jajuOutStr = getCellValue(row, 54);       // 주차(자주식,옥외)
    String kigyeInStr = getCellValue(row, 55);       // 주차(기계식,옥내)
    String kigyeOutStr = getCellValue(row, 56);      // 주차(기계식,옥외)
    String ilbanElevatorStr = getCellValue(row, 57); // 엘리베이터(일반)
    String hwamoolElevatorStr = getCellValue(row, 58); // 엘리베이터(비상/화물)
    String saedaeStr = getCellValue(row, 59);        // 세대수
    String gaguStr = getCellValue(row, 60);          // 가구수
    String heogaeDateStr = getCellValue(row, 61);    // 허가일
    String chakGongDateStr = getCellValue(row, 62);  // 착공일
    String sengInDateStr = getCellValue(row, 63);    // 사용승인일
    String remodelDateStr = getCellValue(row, 64);   // 리모델링일

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

    // 건축물대장 정보가 하나도 없으면 null 반환
    if (minFloor == null && maxFloor == null && landMeter == null && yeonMeter == null &&
        buildingMeter == null && structureStr.isEmpty() && saedae == null && gagu == null) {
      return LedgeInformation.builder()
          .properties(LedgerProperties.builder().build())
          .build();
    }

    // 주소 정보도 포함
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
    String jimokStr = getCellValue(row, 45);      // 기본토지정보 (지목)
    String yongdoStr = getCellValue(row, 46);     // 기본토지용도 (용도지역)

    // 토지 정보가 없으면 null 반환
    if (jimokStr.isEmpty() && yongdoStr.isEmpty()) {
      return LandInformation.builder()
          .properties(LandProperties.builder().build())
          .build();
    }

    // 주소 정보도 포함
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



  @Data
  @Builder
  public static class MemberInfo {

    private String name;
    private String type;
    private String phone;
    private String homePhone;
    private String funnel;
    private String memo;

    public boolean hasValidData() {
      return name != null && !name.trim().isEmpty();
    }

    @Override
    public String toString() {
      return String.format("이름: %s, 분류: %s, 휴대폰: %s, 자택번호: %s, 유입경로: %s",
          name, type, phone, homePhone, funnel);
    }
  }

  private MemberInfo extractMemberInfo(Row row, int memberIndex) {

    int baseCol = 65 + (memberIndex * 8);

    String memberName = getCellValue(row, baseCol);     // 고객명

    // memberName이 없으면 null 반환
    if (memberName == null || memberName.trim().isEmpty() || "-".equals(memberName.trim())) {
      return null;
    }

    String memberType = getCellValue(row, baseCol + 1); // 고객분류
    String memberPhone = getCellValue(row, baseCol + 3); // 고객휴대전화
    String memberHomePhone = getCellValue(row, baseCol + 4); // 고객자택번호
    String memberFunnel = getCellValue(row, baseCol + 6); // 고객유입경로
    String memberMemo = getCellValue(row, baseCol + 8); // 고객메모

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
      case "준비":
        return PropertyStatus.READY;
      case "완료":
        return PropertyStatus.COMPLETE;
      case "보류":
        return PropertyStatus.PENDING;
      case "매각":
        return PropertyStatus.SOLD;
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

  private String buildAddress(String sido, String gugun, String dong, String detail) {
    StringBuilder address = new StringBuilder();

    if (!sido.isEmpty() && !sido.equals("-")) {
      address.append(sido);
    }
    if (!gugun.isEmpty() && !gugun.equals("-")) {
      if (address.length() > 0) {
        address.append(" ");
      }
      address.append(gugun);
    }
    if (!dong.isEmpty() && !dong.equals("-")) {
      if (address.length() > 0) {
        address.append(" ");
      }
      address.append(dong);
    }
    if (!detail.isEmpty() && !detail.equals("-")) {
      if (address.length() > 0) {
        address.append(" ");
      }
      address.append(detail);
    }

    return address.toString();
  }

  private Long parsePriceToLong(String priceStr) {
    if (priceStr == null || priceStr.isEmpty() || priceStr.equals("-")) {
      return null;
    }

    try {
      String cleanPrice = priceStr.replaceAll("[^0-9.]", "");
      if (cleanPrice.isEmpty()) {
        return null;
      }

      double doubleValue = Double.parseDouble(cleanPrice);
      return Math.round(doubleValue);
    } catch (Exception e) {
      log.warn("가격 파싱 실패: {}", priceStr);
      return null;
    }
  }


  private Integer parseIntegerValue(String intStr) {
    if (intStr == null || intStr.isEmpty() || intStr.equals("-")) {
      return null;
    }

    try {
      String cleanInt = intStr.replaceAll("[^0-9]", "");
      if (cleanInt.isEmpty()) {
        return null;
      }

      return Integer.valueOf(cleanInt);
    } catch (NumberFormatException e) {
      log.warn("정수 파싱 실패: {}", intStr);
      return null;
    }
  }

  private Double parseDoubleValue(String doubleStr) {
    if (doubleStr == null || doubleStr.isEmpty() || doubleStr.equals("-")) {
      return null;
    }

    try {
      String cleanDouble = doubleStr.replaceAll("[^0-9.]", "");
      if (cleanDouble.isEmpty()) {
        return null;
      }

      return Double.valueOf(cleanDouble);
    } catch (NumberFormatException e) {
      log.warn("실수 파싱 실패: {}", doubleStr);
      return null;
    }
  }

  private LocalDate parseLocalDate(String dateStr) {
    if (dateStr == null || dateStr.isEmpty() || dateStr.equals("-")) {
      return null;
    }

    try {
      // 다양한 날짜 형식 처리
      dateStr = dateStr.trim();

      // yyyy-MM-dd 형식
      if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
        return LocalDate.parse(dateStr);
      }

      // yyyy.MM.dd 형식
      if (dateStr.matches("\\d{4}\\.\\d{2}\\.\\d{2}")) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
      }

      // yyyy/MM/dd 형식
      if (dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
      }

      // yyyyMMdd 형식 (8자리)
      if (dateStr.matches("\\d{8}")) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
      }

      // yyyyMM 형식 (6자리) - 1일로 설정
      if (dateStr.matches("\\d{6}")) {
        return LocalDate.parse(dateStr + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
      }

      // yyyy 형식 (4자리) - 1월 1일로 설정
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

  private String extractAddressNumber(String detail) {
    if (detail == null || detail.trim().isEmpty()) {
      return detail;
    }
    
    String trimmed = detail.trim();
    log.info("원본 detail: '{}'", trimmed);
    
    // 숫자-숫자 또는 숫자 패턴 추출 (정규식 사용)
    String result = trimmed.replaceAll("^(\\d+(?:-\\d+)?).*", "$1");
    
    log.info("잘린 detail: '{}'", result);
    return result;
  }

  private String getCellValue(Row row, int columnIndex) {
    Cell cell = row.getCell(columnIndex);
    if (cell == null) {
      return "";
    }

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

