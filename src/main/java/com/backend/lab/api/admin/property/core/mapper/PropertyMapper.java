package com.backend.lab.api.admin.property.core.mapper;

import com.backend.lab.api.admin.property.core.dto.resp.BuildingFloorResp;
import com.backend.lab.api.admin.property.core.dto.resp.BuildingLeaseResp;
import com.backend.lab.api.admin.property.core.dto.resp.CustomerUpdatePropertyResp;
import com.backend.lab.api.admin.property.core.dto.resp.LeaseResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAddressResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAdminResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDefaultResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyFloorResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLandResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLedgeResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPriceResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyTemplateResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.openapi.dto.floor.FloorResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.CustomerService;
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
import com.backend.lab.domain.property.core.service.info.AddressInfoService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.entity.dto.resp.SecretResp;
import com.backend.lab.domain.secret.service.SecretService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import com.backend.lab.domain.wishlist.entity.Wishlist;
import com.backend.lab.domain.wishlist.service.WishlistService;
import com.backend.lab.domain.wishlistGroup.service.WishlistGroupService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PropertyMapper {

  private final AdminService adminService;
  private final UploadFileService uploadFileService;
  private final WishlistGroupService wishlistGroupService;
  private final WishlistService wishlistService;
  private final AddressInfoService addressInfoService;
  private final CategoryService categoryService;
  private final SaleService saleService;
  private final DetailsService detailsService;
  private final TaskNoteService taskNoteService;
  private final SecretService secretService;
  private final PropertyMemberService propertyMemberService;
  private final CustomerService customerService;
  private final PropertyAdvertisementService propertyAdvertisementService;

  public PropertySearchResp propertySearchResp(Property property) {
    String bigCategoryName   = property.getBigCategory() != null
        ? property.getBigCategory().getName() : null;
    String smallCategoryName = property.getSmallCategories() != null && !property.getSmallCategories().isEmpty()
        ? property.getSmallCategories().stream().map(Category::getName).collect(Collectors.joining(", ")) : null;

    String groupNameByWishCount = wishlistGroupService.findGroupNameByWishCount(
        property.getWishCount());

    Admin admin = adminService.getByIdNullable(property.getAdminId());

    // 전체 ledge 정보 합산
    Double totalYeonAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getYeonAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getYeonAreaPyeong())
        .sum();
    
    Double totalBuildingAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getBuildingAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getBuildingAreaPyeong())
        .sum();

    Double totalAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getLandAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getLandAreaPyeong())
        .sum();
    Double totalLandAreaPyeng = property.getLand().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getAreaPyung() != null)
        .mapToDouble(ledge -> ledge.getProperties().getAreaPyung())
        .sum();

    // 첫 번째 정보들 (타입, 용도 등)
    LedgeInformation firstLedge = property.getLedge().isEmpty() ? null : property.getLedge().get(0);
    LandInformation firstLand = property.getLand().isEmpty() ? null : property.getLand().get(0);

    UploadFile thumbnailImageUrl = property.getThumbnailImageUrl();
    Sale sale = saleService.getByProperty(property.getId());

    return PropertySearchResp
        .builder()
        .id(property.getId())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())
        .deletedAt(property.getDeletedAt())
        .confirmedAt(property.getConfirmedAt())
        .completedAt(property.getCompletedAt())
        .pendingAt(property.getPendingAt())

        // Sale null 체크
        .sale(sale != null ? saleService.saleResp(sale) : null)

        .isPublic(property.getIsPublic())
        .wishCount(property.getWishCount())
        .wishGroupName(groupNameByWishCount)

        // ThumbnailImage null 체크
        .thumbnailImage(thumbnailImageUrl != null ? uploadFileService.uploadFileResp(thumbnailImageUrl) : null)

        .propertyStatus(property.getStatus())
        .majorName(bigCategoryName)
        .minorName(smallCategoryName)
        .buildingName(property.getBuildingName())

        // FirstLedge Properties null 체크
        .minFloor(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getMinFloor() : null)
        .maxFloor(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getMaxFloor() : null)
        .ledgerType(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getType() : null)

        // 합산된 면적 정보
        .areaPyeng(totalAreaPyeng > 0 ? Math.round(totalAreaPyeng * 100.0) / 100.0 : null)
        .buildingAreaPyeng(totalBuildingAreaPyeng > 0 ? Math.round(totalBuildingAreaPyeng * 100.0) / 100.0 : null)
        .yeonAreaPyeng(totalYeonAreaPyeng > 0 ? Math.round(totalYeonAreaPyeng * 100.0) / 100.0 : null)
        .landAreaPyeong(totalLandAreaPyeng > 0 ? Math.round(totalLandAreaPyeng * 100.0) / 100.0 : null)

        // Price Properties null 체크
        .mmPrice(property.getPrice() != null && property.getPrice().getProperties() != null
            ? property.getPrice().getProperties().getMmPrice() : null)
        .pyengPrice(property.getPrice() != null && property.getPrice().getProperties() != null
            ? property.getPrice().getProperties().getPyeongPrice() : null)
        .roi(property.getPrice() != null && property.getPrice().getProperties() != null
            ? property.getPrice().getProperties().getRoi() : null)

        // MainPurpsCdNm null 체크
        .mainPurpsCdNm(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getMainPurpsCdNm() : null)

        // Yongdo null 체크
        .yongdo(firstLand != null && firstLand.getProperties() != null
            ? firstLand.getProperties().getYongdo() : null)

        // Admin null 체크
        .adminName(buildAdminName(admin))

        .propertyAddressResp(propertyAddressResp(property))
        .build();

  }
  private String buildAdminName(Admin admin) {
    if (admin == null) {
      return "삭제된 관리자";
    }

    StringBuilder adminName = new StringBuilder();

    if (admin.getName() != null) {
      adminName.append(admin.getName());
    }

    if (admin.getJobGrade() != null && admin.getJobGrade().getName() != null) {
      if (!adminName.isEmpty()) {
        adminName.append(" ");
      }
      adminName.append(admin.getJobGrade().getName());
    }

    return !adminName.isEmpty() ? adminName.toString() : null;
  }

  //어드민 지도 조회 전용
  public PropertyListResp propertyAdminMapListResp(Property property) {

    // 전체 ledge 정보 합산
    Double totalYeonAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getYeonAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getYeonAreaPyeong())
        .sum();
    
    Double totalYeonAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getYeonAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getYeonAreaMeter())
        .sum();
    
    Double totalBuildingAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getBuildingAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getBuildingAreaMeter())
        .sum();
    
    Double totalBuildingAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getBuildingAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getBuildingAreaPyeong())
        .sum();

    Double totalLandAreaPyeong = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getLandAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getLandAreaPyeong())
        .sum();

    Double totalLandAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getLandAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getLandAreaMeter())
        .sum();

    // 층 정보 (첫 번째만)
    LedgeInformation firstLedge = property.getLedge().stream().findFirst().orElse(null);

    return PropertyListResp.builder()
        .id(property.getId())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())
        .mmPrice(property.getPrice() != null && property.getPrice().getProperties() != null ?
            property.getPrice().getProperties().getMmPrice() : null)
        .pyeongPrice(property.getPrice() != null && property.getPrice().getProperties() != null ?
            property.getPrice().getProperties().getPyeongPrice() : null)
        .status(property.getStatus())
        .buildingName(property.getBuildingName())
        .thumbnailImage(uploadFileService.uploadFileResp(property.getThumbnailImageUrl()))
        .propertyAddressResp(propertyAddressResp(property))
        .minFloor(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getMinFloor() : null)
        .maxFloor(firstLedge != null && firstLedge.getProperties() != null
            ? firstLedge.getProperties().getMaxFloor() : null)
        .yeonAreaPyeng(totalYeonAreaPyeng > 0 ? Math.round(totalYeonAreaPyeng * 100.0) / 100.0 : null)
        .yeonAreaMeter(totalYeonAreaMeter > 0 ? Math.round(totalYeonAreaMeter * 100.0) / 100.0 : null)
        .buildingAreaMeter(totalBuildingAreaMeter > 0 ? Math.round(totalBuildingAreaMeter * 100.0) / 100.0 : null)
        .buildingAreaPyeng(totalBuildingAreaPyeng > 0 ? Math.round(totalBuildingAreaPyeng * 100.0) / 100.0 : null)
        .areaMeter(totalLandAreaPyeong > 0 ? Math.round(totalLandAreaPyeong * 100.0) / 100.0 : null)
        .areaPyeng(totalLandAreaMeter > 0 ? Math.round(totalLandAreaMeter * 100.0) / 100.0 : null)
        .build();
  }



  public PropertyListResp propertyListResp(Property property) {

    String groupNameByWishCount = wishlistGroupService.findGroupNameByWishCount(property.getWishCount());

    Admin admin = adminService.getByIdNullable(property.getAdminId());

    // 전체 ledge 정보 합산
    Double totalYeonAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getYeonAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getYeonAreaPyeong())
        .sum();
    
    Double totalYeonAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getYeonAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getYeonAreaMeter())
        .sum();
    
    Double totalBuildingAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getBuildingAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getBuildingAreaMeter())
        .sum();
    
    Double totalBuildingAreaPyeng = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getBuildingAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getBuildingAreaPyeong())
        .sum();

    Double totalAreaPyeong = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getLandAreaPyeong() != null)
        .mapToDouble(ledge -> ledge.getProperties().getLandAreaPyeong())
        .sum();

    Double totalAreaMeter = property.getLedge().stream()
        .filter(ledge -> ledge.getProperties() != null && ledge.getProperties().getLandAreaMeter() != null)
        .mapToDouble(ledge -> ledge.getProperties().getLandAreaMeter())
        .sum();

    // 층 정보 (첫 번째만)
    LedgeInformation firstLedge = property.getLedge().stream().findFirst().orElse(null);

    return PropertyListResp.builder()
        .id(property.getId())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())
        .mmPrice(property.getPrice() != null && property.getPrice().getProperties() != null ? 
                property.getPrice().getProperties().getMmPrice() : null)
        .pyeongPrice(property.getPrice() != null && property.getPrice().getProperties() != null ? 
                property.getPrice().getProperties().getPyeongPrice() : null)
        .adminName(buildAdminName(admin))
        .confirmedAt(property.getConfirmedAt())
        .wishCount(property.getWishCount())
        .status(property.getStatus())
        .wishGroupName(groupNameByWishCount)
        .buildingName(property.getBuildingName())
        .thumbnailImage(uploadFileService.uploadFileResp(property.getThumbnailImageUrl()))
        .propertyAddressResp(propertyAddressResp(property))
        .minFloor(firstLedge != null && firstLedge.getProperties() != null 
            ? firstLedge.getProperties().getMinFloor() : null)
        .maxFloor(firstLedge != null && firstLedge.getProperties() != null 
            ? firstLedge.getProperties().getMaxFloor() : null)
        .ledgerType(firstLedge != null && firstLedge.getProperties() != null 
            ? firstLedge.getProperties().getType() : null)
        .areaMeter(totalAreaMeter > 0 ? Math.round(totalAreaMeter * 100.0) / 100.0 : null)
        .areaPyeng(totalAreaPyeong > 0 ? Math.round(totalAreaPyeong * 100.0) / 100.0 : null)
        .yeonAreaPyeng(totalYeonAreaPyeng > 0 ? Math.round(totalYeonAreaPyeng * 100.0) / 100.0 : null)
        .yeonAreaMeter(totalYeonAreaMeter > 0 ? Math.round(totalYeonAreaMeter * 100.0) / 100.0 : null)
        .buildingAreaMeter(totalBuildingAreaMeter > 0 ? Math.round(totalBuildingAreaMeter * 100.0) / 100.0 : null)
        .buildingAreaPyeng(totalBuildingAreaPyeng > 0 ? Math.round(totalBuildingAreaPyeng * 100.0) / 100.0 : null)
        .build();

  }
  public PageResp<PropertyListResp> convertToPageResp(Page<Property> page, Member member) {

    List<PropertyListResp> list = page.getContent().stream()
        .map(property -> {
          PropertyListResp resp = propertyListResp(property);
          Optional<PropertyAdvertisement> advertisement = propertyAdvertisementService.getByPropertyAndAgent(
              property.getId(), member.getId());
          if (advertisement.isPresent()) {
            resp.setStartDate(advertisement.get().getStartDate());
            resp.setEndDate(advertisement.get().getEndDate());
          }
          Wishlist byMemberAndProperty = wishlistService
              .getByMemberAndProperty(member.getId(), property.getId());
          resp.setIsMywish(byMemberAndProperty != null);
          return resp;
        })
        .collect(Collectors.toList());
    return new PageResp<>(page, list);
  }

  public PropertyDefaultResp propertyDefaultResp(Property property, Member member) {
    AddressInformation addressInformation = addressInfoService.getById(
        property.getAddress().getId());
    String groupNameByWishCount = wishlistGroupService.findGroupNameByWishCount(
        property.getWishCount());

    Wishlist byMemberAndProperty = null;
    if (member != null) {
      byMemberAndProperty = wishlistService.getByMemberAndProperty(member.getId(),
          property.getId());
    }
    Boolean isMyWish = false;
    if (byMemberAndProperty != null) {
      isMyWish = true;
    }
    Boolean isMyProperty = false;
    //내 매물 확인
    if (member != null) {
      PropertyMember propertyMember = propertyMemberService.getByMemberAndProperty(
          member.getId(), property.getId());
      if (propertyMember!=null) {
        isMyProperty = true;
      }
    }


    return PropertyDefaultResp.builder()

        .adminId(property.getAdminId())
        .thumbnailImage(uploadFileService.uploadFileResp(property.getThumbnailImageUrl()))
        .confirmedAt(property.getConfirmedAt())
        .isPublic(property.getIsPublic())
        .isExclusive(property.getExclusiveAgentId() != null)
        .exclusiveAgentId(property.getExclusiveAgentId())

        .isMyProperty(isMyProperty)
        .isMywish(isMyWish)
        .wishCount(property.getWishCount())
        .wishGroupName(groupNameByWishCount)
        .sojeji(addressInformation != null && addressInformation.getProperties() != null 
            ? addressInformation.getProperties().getJibunAddress() : null)
        .bigCategory(categoryService.bigCategoryResp(property.getBigCategory()))
        .smallCategories(property.getSmallCategories().stream()
            .map(categoryService::smallCategoryResp)
            .collect(Collectors.toList()))
        .buildingName(property.getBuildingName())
        .status(property.getStatus())
        .build();
  }



  public PropertyUpdateResp propertyUpdateResp(
      Admin admin,
      Property property
  ) {
    List<LandProperties> landProperties = property.getLand().stream()
        .map(LandInformation::getProperties)
        .filter(Objects::nonNull)
        .toList();
    List<LedgerProperties> ledgerProperties = property.getLedge().stream()
        .map(LedgeInformation::getProperties)
        .filter(Objects::nonNull)
        .toList();
    List<BuildingLeaseResp> buildingLeaseRespList = toBuildingLeaseRespList(property);
    AddressProperties address = property.getAddress() != null ? property.getAddress().getProperties() : null;
    PriceProperties price = property.getPrice() != null ? property.getPrice().getProperties() : null;
    RegisterProperties register = property.getRegister() != null ? property.getRegister().getProperties() : null;
    Details details = detailsService.getByPropertyId(property.getId());

    List<UploadFileResp> propertyImages = details.getPropertyImages().stream().map(
        uploadFileService::uploadFileResp
    ).toList();

    List<UploadFileResp> dataImages = details.getDataImages().stream().map(
        uploadFileService::uploadFileResp
    ).toList();

    DetailsResp detailsResp = detailsService.detailsResp(details, propertyImages, dataImages);

    List<TaskNoteResp> taskNoteResps = taskNoteService.getByProperty(property.getId()).stream()
        .map(taskNoteService::taskNoteResp).toList();

    List<Secret> secrets = new ArrayList<>();
    Secret secret = secretService.getByPropertyAndAdmin(property.getId(), admin.getId());
    if (secret != null) {
      secrets.add(secret);
    }

    Long adminId = admin.getId();
    Department department = admin.getDepartment();
    Permission permission = admin.getPermission();
    SecretMemoExposeLevel level = permission == null ? null : permission.getSecretMemoExposeLevel();

    List<SecretResp> secretResps;
    if (admin.getRole().equals(AdminRole.ADMIN)) {
      secretResps = secrets.stream()
          .map(secretService::secretResp)
          .toList();
    } else {
      secretResps = secretService
          .filter(adminId,department,level,secrets)
          .stream()
          .map(secretService::secretResp)
          .toList();
    }
    List<PropertyMember> propertyMembers = propertyMemberService.getByProperty(property.getId());

    List<CustomerUpdatePropertyResp> memberRespList = new ArrayList<>();

    if (!propertyMembers.isEmpty()) {
      List<Long> memberIds = propertyMembers.stream().map(PropertyMember::getMemberId
      ).toList();

      List<Member> memberList = memberIds.stream().map(customerService::getById).toList();

      memberRespList = memberList.stream()
          .map(this::customerUpdatePropertyResp).toList();
    }

    PropertyDefaultResp propertyDefaultResp = propertyDefaultResp(property, null);

    return PropertyUpdateResp.builder()
        .id(property.getId())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())

        .lands(landProperties)
        .ledges(ledgerProperties)
        .address(address)
        .price(price)
        .leaseStatus(buildingLeaseRespList)
        .taskNote(taskNoteResps)
        .detail(detailsResp)
        .secret(secretResps)
        .members(memberRespList)
        .register(register)
        .defaults(propertyDefaultResp)
        .template(propertyTemplateResp(property))
        .build();
  }

  public PropertyTemplateResp propertyTemplateResp(Property property) {

    TemplateProperties template = property.getTemplate() != null ? property.getTemplate().getProperties() : null;

    if (template == null) {
      return PropertyTemplateResp.builder().build(); // 모든 필드가 null
    }


    return PropertyTemplateResp.builder()
        .info1Url(uploadFileService.uploadFileResp(template.getInfo1Url()))
        .info2Url(uploadFileService.uploadFileResp(template.getInfo2Url()))
        .planPartUrl(uploadFileService.uploadFileResp(template.getPlanPartUrl()))
        .planEntireUrl(uploadFileService.uploadFileResp(template.getPlanEntireUrl()))
        .etc1Url(uploadFileService.uploadFileResp(template.getEtc1Url()))
        .etc2Url(uploadFileService.uploadFileResp(template.getEtc2Url()))
        .etc3Url(uploadFileService.uploadFileResp(template.getEtc3Url()))
        .etc4Url(uploadFileService.uploadFileResp(template.getEtc4Url()))
        .etc5Url(uploadFileService.uploadFileResp(template.getEtc5Url()))
        .etc6Url(uploadFileService.uploadFileResp(template.getEtc6Url()))
        .etc7Url(uploadFileService.uploadFileResp(template.getEtc7Url()))
        .etc8Url(uploadFileService.uploadFileResp(template.getEtc8Url()))
        .etc9Url(uploadFileService.uploadFileResp(template.getEtc9Url()))
        .etc10Url(uploadFileService.uploadFileResp(template.getEtc10Url()))
        .build();
  }

  public PropertyAddressResp propertyAddressResp(Property property) {
    if (property == null || property.getAddress() == null || property.getAddress().getProperties() == null) {
      return PropertyAddressResp.builder().build();
    }
    String landNum = null;
    AddressProperties address = property.getAddress().getProperties();
    
    String jibunAddress = address.getJibunAddress();
    if (jibunAddress != null && jibunAddress.contains(",")) {
        jibunAddress = jibunAddress.substring(0, jibunAddress.indexOf(","));
    }
    
    if (property.getLand() != null && property.getLand().size() > 1) {
        landNum = " 외 " + (property.getLand().size() - 1) + "필지";
    }
    

    return PropertyAddressResp.builder()
        .pnu(address.getPnu())
        .lat(address.getLat())
        .lng(address.getLng())
        .jibunAddress(jibunAddress + (landNum != null ? landNum : ""))
        .roadAddress(address.getRoadAddress() + (landNum != null ? landNum : ""))
        .build();
  }


  public CustomerUpdatePropertyResp customerUpdatePropertyResp(Member member) {

    if (member.getType()== MemberType.CUSTOMER){

      return CustomerUpdatePropertyResp.builder()
          .canUpdate(true)
          .etc(member.getCustomerProperties().getEtc())
          .memberId(member.getId())
          .name(member.getCustomerProperties().getName())
          .phoneNumber(member.getCustomerProperties().getPhoneNumber())
          .companyType(member.getCustomerProperties().getCompanyType())
          .note(member.getCustomerProperties().getNote())
          .funnel(member.getCustomerProperties().getFunnel())
          .homePhoneNumber(member.getCustomerProperties().getHomePhoneNumber())
          .build();
    }
    else {
      return CustomerUpdatePropertyResp.builder()
          .memberId(member.getId())
          .canUpdate(false)
          .etc(member.getCustomerProperties().getEtc())
          .companyType(member.getSellerProperties().getCompanyType())
          .phoneNumber(member.getSellerProperties().getPhoneNumber())
          .name(member.getSellerProperties().getName())
          .note(member.getCustomerProperties().getNote())
          .funnel(member.getCustomerProperties().getFunnel())
          .homePhoneNumber(member.getCustomerProperties().getHomePhoneNumber())
          .build();
    }
  }


  ///-------------------------매물 상세조회 mapper------------------------///
  public PropertyAdminResp propertyAdminResp(Property property) {
    Long adminId = property.getAdminId();
    Admin admin = adminService.getByIdNullable(adminId);

    if (admin == null) {
      return PropertyAdminResp.builder()
          .name("삭제된 관리자")
          .build();
    }

    return PropertyAdminResp.builder()
        .name(admin.getName())
        .phoneNumber(admin.getPhoneNumber())
        .email(admin.getEmail())
        .jobGrade(admin.getJobGrade() != null ? admin.getJobGrade().getName() : null)
        .build();
  }

  public PropertyPriceResp propertyPriceResp(Property property) {
    if (property == null || property.getPrice() == null || property.getPrice().getProperties() == null) {
      return PropertyPriceResp.builder().build();}

    PriceProperties properties = property.getPrice().getProperties();
    return PropertyPriceResp.builder()
        .mmPrice(properties.getMmPrice())
        .igPrice(properties.getIgPrice())
        .etcPrice(properties.getEtcPrice())
        .yeonPyongPrice(properties.getYeonPyongPrice())
        .roi(properties.getRoi())
        .pyeongPrice(properties.getPyeongPrice())
        .depositPrice(properties.getDepositPrice())
        .monthPrice(properties.getMonthPrice())
        .grPrice(properties.getGrPrice())
        .grEtc(properties.getGrEtc())
        .grOut(properties.getGrOut())
        .loanDescription(properties.getLoanDescription())
        .build();
  }

  public List<PropertyLedgeResp> propertyLedgeResp(Property property) {
    return property.getLedge().stream()
        .map(this::toLedgeResp)
        .toList();
  }

  private PropertyLedgeResp toLedgeResp(LedgeInformation ledge) {
    LedgerProperties props = ledge.getProperties();
    
    if (props == null) {
      return PropertyLedgeResp.builder()
          .buildingOrder(ledge.getBuildingOrder())
          .build();
    }

    return PropertyLedgeResp.builder()
        .buildingOrder(ledge.getBuildingOrder())
        .jibunAddress(props.getJibunAddress())
        .minFloor(props.getMinFloor())
        .maxFloor(props.getMaxFloor())
        .mainPurpsCdNm(props.getMainPurpsCdNm())
        .structure(props.getStructure())
        .landAreaMeter(props.getLandAreaMeter() != null ? Math.round(props.getLandAreaMeter() * 100.0) / 100.0 : null)
        .landAreaPyeong(props.getLandAreaPyeong() != null ? Math.round(props.getLandAreaPyeong() * 100.0) / 100.0 : null)
        .buildingAreaMeter(props.getBuildingAreaMeter() != null ? Math.round(props.getBuildingAreaMeter() * 100.0) / 100.0 : null)
        .buildingAreaPyeong(props.getBuildingAreaPyeong() != null ? Math.round(props.getBuildingAreaPyeong() * 100.0) / 100.0 : null)
        .yeonAreaMeter(props.getYeonAreaMeter() != null ? Math.round(props.getYeonAreaMeter() * 100.0) / 100.0 : null)
        .yeonAreaPyeong(props.getYeonAreaPyeong() != null ? Math.round(props.getYeonAreaPyeong() * 100.0) / 100.0 : null)
        .yongjeokAreaMeter(props.getYongjeokAreaMeter() != null ? Math.round(props.getYongjeokAreaMeter() * 100.0) / 100.0 : null)
        .yongjeokAreaPyeong(props.getYongjeokAreaPyeong() != null ? Math.round(props.getYongjeokAreaPyeong() * 100.0) / 100.0 : null)
        .ilbanElevator(props.getIlbanElevator())
        .hwamoolElevator(props.getHwamoolElevator())
        .doroWidthMeter(props.getDoroWidthMeter() != null ? props.getDoroWidthMeter() : null)
        .jajuInParking(props.getJajuInParking())
        .jajuOutParking(props.getJajuOutParking())
        .kigyeInParking(props.getKigyeInParking())
        .kigyeOutParking(props.getKigyeOutParking())
        .elcInParking(props.getElcInParking())
        .elcOutParking(props.getElcOutParking())
        .geonPye(props.getGeonPye() != null ? props.getGeonPye() : null)
        .yongjeok(props.getYongjeok() != null ? props.getYongjeok() : null)
        .etcUsage(props.getEtcUsage())
        .owner(props.getOwner())
        .saedae(props.getSaedae())
        .gagu(props.getGagu())
        .height(props.getHeight() != null ? props.getHeight() : null)
        .jiboong(props.getJiboong())
        .heogaeDate(props.getHeogaeDate())
        .chakGongDate(props.getChakGongDate())
        .sengInDate(props.getSengInDate())
        .remodelDate(props.getRemodelDate())
        .yangmyen(props.getYangmyen())
        .coner(props.getConer())
        .type(props.getType())
        .build();
  }

  public PropertyFloorResp toPropertyFloorResp(Property property) {
    return PropertyFloorResp.builder()
        .buildingFloors(toBuildingFloorRespList(property))
        .leaseStatus(toBuildingLeaseRespList(property))
        .build();
  }

  /**
   * 빌딩별 층정보 목록 생성 (FloorInformation 기반)
   */
  private List<BuildingFloorResp> toBuildingFloorRespList(Property property) {
    // FloorInformation을 buildingOrder별로 그룹핑
    Map<Long, List<FloorInformation>> floorsByBuilding = property.getFloors().stream()
        .collect(Collectors.groupingBy(
            FloorInformation::getBuildingOrder,
            () -> new LinkedHashMap<>(),
            Collectors.toList()
        ));

    return floorsByBuilding.entrySet().stream()
        .map(entry -> toBuildingFloorResp(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(BuildingFloorResp::getBuildingOrder,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();
  }

  /**
   * 개별 빌딩의 층정보 생성 (FloorInformation에서 FloorResp 생성)
   */
  private BuildingFloorResp toBuildingFloorResp(Long buildingOrder, List<FloorInformation> floors) {
    // rank 순서로 정렬하여 FloorResp 생성
    List<FloorResp> floorRespList = floors.stream()
        .sorted(Comparator.comparing(FloorInformation::getRank,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(this::toFloorResp)
        .toList();

    return BuildingFloorResp.builder()
        .buildingOrder(buildingOrder)
        .floors(floorRespList)
        .build();
  }

  /**
   * FloorInformation을 FloorResp로 변환
   */
  private FloorResp toFloorResp(FloorInformation floorInfo) {
    FloorProperties props = floorInfo.getProperties();
    
    if (props == null) {
      return FloorResp.builder().build();
    }

    return FloorResp.builder()
        .floor(props.getFloor())
        .areaPyung(props.getAreaPyung())
        .mainPurps(extractMainPurpose(props.getUpjong())) // 업종에서 주용도 추출
        .etcPurps(extractEtcPurpose(props.getUpjong()))   // 업종에서 기타용도 추출
        .build();
  }

  /**
   * 빌딩별 임대차 현황 목록 생성 (FloorInformation 기반)
   */
  private List<BuildingLeaseResp> toBuildingLeaseRespList(Property property) {
    // FloorInformation을 buildingOrder별로 그룹핑
    Map<Long, List<FloorInformation>> floorsByBuilding = property.getFloors().stream()
        .filter(floor -> Boolean.TRUE.equals(floor.getIsPublic())) // 공개된 것만
        .collect(Collectors.groupingBy(
            FloorInformation::getBuildingOrder,
            LinkedHashMap::new,
            Collectors.toList()
        ));

    return floorsByBuilding.entrySet().stream()
        .map(entry -> toBuildingLeaseResp(entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(BuildingLeaseResp::getBuildingOrder,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();
  }

  /**
   * 개별 빌딩의 임대차 현황 생성
   */
  private BuildingLeaseResp toBuildingLeaseResp(Long buildingOrder, List<FloorInformation> floors) {
    List<FloorInformation> sortedFloors = floors.stream()
        .sorted(Comparator.comparing(FloorInformation::getRank,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .toList();

    // LeaseResp 생성
    List<LeaseResp> leaseDetails = sortedFloors.stream()
        .map(this::toLeaseResp)
        .toList();

    // 첫 번째 층의 isPublic 값 사용
    Boolean isPublic = sortedFloors.isEmpty() ? true :
        sortedFloors.get(0).getIsPublic();

    return BuildingLeaseResp.builder()
        .buildingOrder(buildingOrder)
        .isPublic(isPublic != null ? isPublic : true)
        .leaseDetails(leaseDetails)
        .build();

  }

  /**
   * FloorInformation을 LeaseResp로 변환
   */
  private LeaseResp toLeaseResp(FloorInformation floorInfo) {
    FloorProperties props = floorInfo.getProperties();
    
    if (props == null) {
      return LeaseResp.builder().build();
    }

    return LeaseResp.builder()
        .floor(props.getFloor())
        .areaPyung(props.getAreaPyung())
        .areaMeter(props.getAreaMeter())
        .upjong(props.getUpjong())
        .depositPrice(props.getDepositPrice())
        .monthlyPrice(props.getMonthlyPrice())
        .grPrice(props.getGrPrice())
        .etc(props.getEtc())
        .isEmpty(props.getIsEmpty())
        .isHidden(props.getIsHidden())
        .build();
  }

  /**
   * 업종에서 주용도 추출 (예: "제1종근린생활시설(소매점)" -> "제1종근린생활시설")
   */
  private String extractMainPurpose(String upjong) {
    if (upjong == null || upjong.trim().isEmpty()) {
      return null;
    }

    // 괄호 앞까지를 주용도로 추출
    int openParenIndex = upjong.indexOf('(');
    if (openParenIndex > 0) {
      return upjong.substring(0, openParenIndex).trim();
    }

    // 괄호가 없으면 전체를 주용도로 반환
    return upjong.trim();
  }

  /**
   * 업종에서 기타용도 추출 (예: "제1종근린생활시설(소매점)" -> "소매점")
   */
  private String extractEtcPurpose(String upjong) {
    if (upjong == null || upjong.trim().isEmpty()) {
      return null;
    }

    // 괄호 안의 내용을 기타용도로 추출
    int openParenIndex = upjong.indexOf('(');
    int closeParenIndex = upjong.indexOf(')', openParenIndex);

    if (openParenIndex >= 0 && closeParenIndex > openParenIndex) {
      String etcPurpose = upjong.substring(openParenIndex + 1, closeParenIndex).trim();
      return etcPurpose.isEmpty() ? null : etcPurpose;
    }

    // 괄호가 없거나 형식이 맞지 않으면 null 반환
    return null;
  }

  public List<PropertyLandResp> propertyLandResp(Property property) {

    return property.getLand().stream()
        .map(landInformation -> toLandResp(landInformation,property))
        .toList();
  }

  private PropertyLandResp toLandResp(LandInformation land,Property property) {
    LandProperties props = land.getProperties();
    
    if (props == null) {
      return PropertyLandResp.builder().build();
    }

    if (props.getJibunAddress() == null && property.getAddress() != null) {
      AddressProperties addrProps = property.getAddress().getProperties();
      if (addrProps != null && addrProps.getJibunAddress() != null) {
        props.setJibunAddress(addrProps.getJibunAddress());
      }
    }

    return PropertyLandResp.builder()
        .jibunAddress(props.getJibunAddress())
        .buildingOrder(land.getBuildingOrder())
        .areaMeter(props.getAreaMeter())
        .areaPyung(props.getAreaPyung())
        .gonsiPricePerMeter(props.getGonsiPricePerMeter())
        .gonsiPricePerPyung(props.getGonsiPricePerPyung())
        .totalGonsiPrice(props.getTotalGonsiPrice())
        .jimok(props.getJimok())
        .yongdo(props.getYongdo())
        .iyongSituation(props.getIyongSituation())
        .ownerType(props.getOwnerType())
        .ownerChangeDate(props.getOwnerChangeDate())
        .ownerChangeReason(props.getOwnerChangeReason())
        .doroJeopMyun(props.getDoroJeopMyun())
        .landHeight(props.getLandHeight())
        .landShape(props.getLandShape())
        .phUsagePlanWithLaw(props.getPhUsagePlanWithLaw())
        .jcUsagePlanWithLaw(props.getJcUsagePlanWithLaw())
        .jhUsagePlanWithLaw(props.getJhUsagePlanWithLaw())
        .phUsagePlanWithEtc(props.getPhUsagePlanWithEtc())
        .jcUsagePlanWithEtc(props.getJcUsagePlanWithEtc())
        .jhUsagePlanWithEtc(props.getJhUsagePlanWithEtc())
        .build();
  }


}
