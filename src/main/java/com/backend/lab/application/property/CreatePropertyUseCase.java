package com.backend.lab.application.property;

import com.backend.lab.api.admin.property.core.dto.req.PropertyAddressReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyDefaultReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyFloorReq;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyMemberFacade;
import com.backend.lab.api.admin.property.core.facade.TemplateFacade;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.details.entity.dto.req.DetailReq;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.notification.entity.vo.NotificationType;
import com.backend.lab.domain.notification.service.NotificationService;
import com.backend.lab.domain.property.category.service.CategoryService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.entity.information.PriceInformation;
import com.backend.lab.domain.property.core.entity.information.RegisterInformation;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.core.service.info.AddressInfoService;
import com.backend.lab.domain.property.core.service.info.FloorInfoService;
import com.backend.lab.domain.property.core.service.info.LandInfoService;
import com.backend.lab.domain.property.core.service.info.LedgeInfoService;
import com.backend.lab.domain.property.core.service.info.PriceInfoService;
import com.backend.lab.domain.property.core.service.info.RegisterInfoService;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreatePropertyUseCase {

  private final AdminService adminService;
  private final AddressInfoService addressInfoService;
  private final PriceInfoService priceInfoService;
  private final RegisterInfoService registerInfoService;
  private final TemplateFacade templateFacade;
  private final UploadFileService uploadFileService;
  private final CategoryService categoryService;
  private final PropertyService propertyService;
  private final TaskNoteService taskNoteService;
  private final SecretService secretService;
  private final LedgeInfoService ledgeInfoService;
  private final LandInfoService landInfoService;
  private final PropertyChangeDetectService propertyChangeDetectService;
  private final MemberService memberService;
  private final NotificationService notificationService;
  private final DetailsService detailsService;
  private final AdminPropertyMemberFacade adminPropertyMemberFacade;
  private final FloorInfoService floorInfoService;


  @Transactional
  public void execute(PropertyCreateReq req, Long adminId, String clientIp){
    Admin admin = adminService.getById(adminId);

    //property 객체 생성
    Property property = mapToProperty(req);

    Property savedProperty = propertyService.create(property);

    createRelatedData(req, savedProperty, admin, clientIp);
    createInfoList(req.getLands(), req.getLedges(), req.getFloors(), savedProperty.getId());

    //매물작업이력생성
    propertyChangeDetectService.PropertyCreateWorkLog(req, savedProperty, admin, clientIp);
    List<Member> agents = memberService.getsAgent();
    agents.forEach(
        agent -> notificationService.createByProperty(agent.getId(), property.getId(),
            property.getBuildingName(),
            NotificationType.REGISTER));
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


  protected void createRelatedData(PropertyCreateReq req, Property property, Admin admin,
      String clientIp) {
    // 업무일지
    if (req.getTaskNote() != null) {
      taskNoteService.createByProperty(req.getTaskNote(), property, admin);
    }

    // 비밀메모
    if (req.getSecret() != null) {
      secretService.create(req.getSecret(), property, admin);
    }

    // 상세정보
    if (req.getDetail() != null) {
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
      detailsService.create(req.getDetail(), property);
    }
    else if (req.getDetail()==null) {
      detailsService.create(new DetailReq(), property);
    }

    adminPropertyMemberFacade.createPropertyMember(req.getMembers(), property.getId(),
        admin.getId(), clientIp);

  }


  private Property mapToProperty(PropertyCreateReq req) {

    AddressInformation addressInformation = addressInfoService.create(
        req.getAddress() != null ? req.getAddress() : new PropertyAddressReq());

    Long yeonPyongPrice = req.getPrice().calculateYeonPyongPrice(req.getLedges());

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
}
