package com.backend.lab.application.property;


import com.backend.lab.api.admin.property.core.dto.req.PropertyFloorReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyTemplateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyMemberFacade;
import com.backend.lab.api.admin.property.core.facade.TemplateFacade;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.service.CategoryService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.core.service.info.AddressInfoService;
import com.backend.lab.domain.property.core.service.info.FloorInfoService;
import com.backend.lab.domain.property.core.service.info.LandInfoService;
import com.backend.lab.domain.property.core.service.info.LedgeInfoService;
import com.backend.lab.domain.property.core.service.info.PriceInfoService;
import com.backend.lab.domain.property.core.service.info.RegisterInfoService;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.propertyWorkLog.service.PropertyChangeDetectService;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.secret.service.SecretService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UpdatePropertyUseCase {


  private final AdminService adminService;
  private final PropertyService propertyService;
  private final CategoryService categoryService;
  private final PropertyChangeDetectService propertyChangeDetectService;
  private final TaskNoteService taskNoteService;
  private final AddressInfoService addressInfoService;
  private final PriceInfoService priceInfoService;
  private final RegisterInfoService registerInfoService;
  private final TemplateFacade templateFacade;
  private final SecretService secretService;
  private final UploadFileService uploadFileService;
  private final DetailsService detailsService;
  private final AdminPropertyMemberFacade adminPropertyMemberFacade;
  private final LedgeInfoService ledgeInfoService;
  private final LandInfoService landInfoService;
  private final FloorInfoService floorInfoService;

  @Transactional
  public void execute(PropertyUpdateReq req, Long adminId, Long propertyId,
      String clientIp) {
    Admin admin = adminService.getById(adminId);
    Property property = propertyService.getById(propertyId);
    Category bigCateogry = categoryService.getById(req.getDefaults().getBigCategoryId());
    Set<Category> smallCategories = req.getDefaults().getSmallCategoryIds() != null ?
        req.getDefaults().getSmallCategoryIds().stream()
            .map(categoryService::getById)
            .collect(Collectors.toSet()) : new LinkedHashSet<>();

    propertyChangeDetectService.PropertyUpdateWorkLog(req, property, admin, clientIp);

    createAutoTaskNote(req, property, admin);
    updateInformation(req, property);
    updateRelateDate(req, property, admin, clientIp);
    updateInfoList(req, propertyId);

    Long thumbnailId = req.getDefaults().getThumbnailId();
    if (thumbnailId != null) {
      UploadFile thumbnail = uploadFileService.getById(thumbnailId);
      req.getDefaults().setThumbnailImageUrl(thumbnail);
    }
    propertyService.update(propertyId, req, bigCateogry, smallCategories);

  }



  private void updateInfoList(PropertyUpdateReq req, Long propertyId) {
    Property property = propertyService.getById(propertyId);

    // 기존 정보 모두 삭제
    deleteInfoList(property);

    // 새로운 정보 생성 (create 로직 재사용)
    createInfoList(req.getLands(), req.getLedges(), req.getFloors(), propertyId);

  }
  private void deleteInfoList(Property property) {
    // Floor 삭제
    if (property.getFloors() != null && !property.getFloors().isEmpty()) {
      Set<Long> floorIds = property.getFloors().stream()
          .map(FloorInformation::getId)
          .collect(Collectors.toSet());
      property.getFloors().clear();
      floorIds.forEach(floorInfoService::deleteSingleFloor);
    }

    // Ledge 삭제
    if (property.getLedge() != null && !property.getLedge().isEmpty()) {
      List<Long> ledgeIds = property.getLedge().stream()
          .map(LedgeInformation::getId)
          .toList();
      property.getLedge().clear();
      ledgeIds.forEach(ledgeInfoService::delete);
    }

    // Land 삭제
    if (property.getLand() != null && !property.getLand().isEmpty()) {
      List<Long> landIds = property.getLand().stream()
          .map(LandInformation::getId)
          .toList();
      property.getLand().clear();
      landIds.forEach(landInfoService::delete);
    }
  }

  private void createInfoList(List<LandProperties> lands, List<LedgerProperties> ledges,
      List<PropertyFloorReq> floors, Long propertyId) {
    Property property = propertyService.getById(propertyId);

    Set<FloorInformation> allFloorInfos = new LinkedHashSet<>();

    if (ledges == null) {
      ledges = new ArrayList<>();
    }
    if (lands == null) {
      lands = new ArrayList<>();
    }
    if (floors == null) {
      floors = new ArrayList<>();
    }

    int maxSize = Math.max(Math.max(ledges.size(), lands.size()), floors.size());

    if (maxSize == 0) {
      return;
    }
    //한 건물에 여러개 토지 가능, 한 토지에 여러개 건물도 가능
    //토지는 그냥 리스트로 뿌려주고 건물이랑 층만 잘 buildingOrder매겨서 넣어주면 될
    for (int i = 0; i < maxSize; i++) {
      Long buildingOrder = (long) i + 1;

      if (i < ledges.size()) {
        LedgeInformation ledgeInformation = ledgeInfoService.create(LedgeInformation.builder()
            .properties(ledges.get(i))
            .buildingOrder(buildingOrder)
            .build());
        property.getLedge().add(ledgeInformation);
      }
      if (i < lands.size()) {
        LandInformation landInformation = landInfoService.create(LandInformation.builder()
            .properties(lands.get(i))
            .buildingOrder(buildingOrder)
            .build());
        property.getLand().add(landInformation);
      }
      if (i < floors.size()) {
        //각 빌딩마다 수행한다.
        PropertyFloorReq propertyFloorReq = floors.get(i); //req에서 set꺼내서 하나씩 비교함
        if (propertyFloorReq != null && !propertyFloorReq.getFloor().isEmpty()) {
          Set<FloorInformation> floorInformations = floorInfoService.floorsAssignRank(
              propertyFloorReq.getFloor(),//층 하나들어감
              propertyFloorReq.getIsPublic(),
              buildingOrder);
          allFloorInfos.addAll(floorInformations);
        }
      }
    }
    property.setFloors(allFloorInfos);
  }

  private void updateRelateDate(PropertyUpdateReq req, Property property, Admin admin,
      String clientIp) {
    //업무일지
    if (req.getTaskNote() != null) {
      taskNoteService.updateByProperty(req.getTaskNote(), property, admin);
    }

    // 비밀메모
    if (req.getSecret() != null) {
      secretService.create(req.getSecret(), property, admin);
    }
    // 상세정보
    if (req.getDetail() != null) {
      // 이미지 처리를 공통으로 먼저 수행
      if (req.getDetail().getPropertyImageIds() != null) {
        List<UploadFile> propertyImages = uploadFileService.getByIds(
            req.getDetail().getPropertyImageIds()
        );
        req.getDetail().setPropertyImages(propertyImages);
      }
      if (req.getDetail().getDataImageIds() != null) {
        List<UploadFile> dataImages = uploadFileService.getByIds(
            req.getDetail().getDataImageIds()
        );
        req.getDetail().setDataImages(dataImages);
      }

      // 기존 상세정보 존재 여부 확인
      if (detailsService.getByPropertyId(property.getId()) != null) {
        detailsService.update(req.getDetail(), property);
      } else {
        detailsService.create(req.getDetail(), property);
      }
    }
    if (req.getMembers() != null) {
      adminPropertyMemberFacade.updatePropertyMember(req.getMembers(), property.getId(),
          admin.getId(), clientIp);
    }
  }


  private void updateInformation(PropertyUpdateReq req, Property property) {

    if (req.getAddress() != null) {
      addressInfoService.update(req.getAddress(), property.getAddress().getId());
    }

    if (req.getPrice() != null) {
      Long yeonPyongPrice = req.getPrice().calculateYeonPyongPrice(req.getLedges());
      priceInfoService.update(req.getPrice(), property.getPrice().getId(), yeonPyongPrice,req.getLands());
    }

    registerInfoService.update(req.getRegister() != null ? req.getRegister() : new RegisterProperties(), property.getRegister().getId());

    templateFacade.updateTemplateInformation(
        req.getTemplate() != null ? req.getTemplate() : new PropertyTemplateReq(),
        property.getTemplate().getId()
    );

  }




  private void createAutoTaskNote(PropertyUpdateReq req, Property property, Admin admin) {
    //각 감지마다 생성해야 함
    List<String> members = propertyChangeDetectService.detectPhoneNumber(req, admin, property);
    if (members != null) {
      createTaskNote(members, property, admin, LogFieldType.PHONE_NUMBER);
    }
    List<String> admins = propertyChangeDetectService.detectAdmin(req, property);
    if (admins != null) {
      createTaskNote(admins, property, admin, LogFieldType.PHONE_NUMBER);
    }
    List<String> mmPrice = propertyChangeDetectService.detectMMPrice(req, property);
    if (mmPrice != null) {
      createTaskNote(mmPrice, property, admin, LogFieldType.MM_PRICE);
    }
    List<String> pyengPrice = propertyChangeDetectService.detectPyengPrice(req, property);
    if (pyengPrice != null) {
      createTaskNote(pyengPrice, property, admin, LogFieldType.PYENG_PRICE);
    }
    List<String> status = propertyChangeDetectService.detectStatus(req, property);
    if (status != null) {
      createTaskNote(status, property, admin, LogFieldType.STATUS);
    }
    List<String> roi = propertyChangeDetectService.detectRoi(req, property);
    if (roi != null) {
      createTaskNote(roi, property, admin, LogFieldType.ROI);
    }
    List<String> depositPrice = propertyChangeDetectService.detectDepositPrice(req, property);
    if (depositPrice != null) {
      createTaskNote(depositPrice, property, admin, LogFieldType.DEPOSIT_PRICE);
    }
    List<String> monthPrice = propertyChangeDetectService.detectMonthPrice(req, property);
    if (monthPrice != null) {
      createTaskNote(monthPrice, property, admin, LogFieldType.MONTH_PRICE);
    }

  }

  private void createTaskNote(List<String> changes, Property property, Admin admin,
      LogFieldType logFieldType) {
    String before = changes.get(0);
    String after = changes.get(1);
    taskNoteService.save(before, after, property, admin, logFieldType);
  }


}
