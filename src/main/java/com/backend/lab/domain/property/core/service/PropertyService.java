package com.backend.lab.domain.property.core.service;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAdminMapDto;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAddressResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyAdminResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDefaultResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyFloorResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLandResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyLedgeResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPriceResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyTemplateResp;
import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.core.dto.req.PropertyMemberOptions;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.repository.PropertyDeletedRepository;
import com.backend.lab.domain.property.core.repository.PropertyRepository;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyService {
  private final PropertyRepository propertyRepository;
  private final PropertyDeletedRepository propertyDeletedRepository;

  public Property getById(Long id) {
    return propertyRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Property"));
  }

  public Property getDeletedById(Long id) {
    return propertyDeletedRepository.getDeletedById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Property"));
  }

  public List<Property> getsByPnuStartWith(String pnu) {
    return propertyRepository.findByPnuStartWith(pnu);
  }

  public List<Property> getsByBigCategoryId(Long id) {
    return propertyRepository.findAllByBigCategoryId(id);
  }

  public List<Property> getsBySmallCategoryId(Long id) {
    return propertyRepository.findAllBySmallCategoryId(id);
  }

  public long countByAdminId(Long adminId) {
    return propertyRepository.countByAdminId(adminId);
  }

  public Property getByIdWithAllDetails(Long id) {
    return propertyRepository.findByIdWithAllDetails(id).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Property"));
  }

  public Property getByIdWithFloors(Long id) {
    return propertyRepository.findWithFloors(id).orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Property"));
  }


  @Transactional
  public Property create(Property property) {
    return propertyRepository.save(property);
  }

  public Page<Property> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    return propertyRepository.search(options,adminIds,bigCategoryIds,smallCategoryIds);
  }

  public Page<Property> searchDeleted(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    return propertyDeletedRepository.search(options,adminIds,bigCategoryIds,smallCategoryIds);
  }

  public Page<Property> getsByAgentAdvAvailable(Member agent, Pageable pageable) {
    return propertyRepository.getsByAgentAdvAvailable(agent, LocalDateTime.now(), pageable);
  }

  public Page<Property> getsByAgentAdvAll(Member agent, Pageable pageable) {
    return propertyRepository.getsByAgentAdvAvailable(agent, pageable);
  }

  public Page<Property> getsByExclusiveAgentId(Long id, Pageable pageable) {
    return propertyRepository.getsByExclusiveAgentId(id, pageable);
  }

  public Page<Property> getsWithPropertyMember(Long memberId, Pageable pageable) {
    return propertyRepository.getsWithPropertyMember(memberId, pageable);
  }

  public Page<Property> wishlistSearch(SearchWishlistOptions options) {
    return propertyRepository.wishlistSearch(options);
  }

  public Page<Property> memberWishlist(PageOptions options, Long memberId) {
    return propertyRepository.memberWishlist(options, memberId);
  }

  @Transactional
  public void update(Long id, PropertyUpdateReq req, Category bigCategory, Set<Category> smallCategories) {
    Property property = getById(id);

    property.setThumbnailImageUrl(req.getDefaults().getThumbnailImageUrl());
    property.setIsPublic(req.getDefaults().getIsPublic());
    property.setBigCategory(bigCategory);
    property.getSmallCategories().clear();
    property.getSmallCategories().addAll(smallCategories);
    property.setExclusiveAgentId(req.getDefaults().getExclusiveId());
    property.setStatus(req.getDefaults().getStatus());
    property.setBuildingName(req.getDefaults().getBuildingName());
    property.setAdminId(req.getDefaults().getAdminId());

  }

  public PropertyResp propertyResp(Property property, PropertyAdminResp propertyAdminResp,
      PropertyDefaultResp propertyDefaultResp, PropertyPriceResp propertyPriceResp,
      List<PropertyLedgeResp> propertyLedgeResps, PropertyFloorResp propertyFloorResp,
      List<PropertyLandResp> propertyLandResps, DetailsResp detailsResp, PropertyAddressResp addressResp,SaleResp saleResp,
      PropertyTemplateResp templateResp) {

    return PropertyResp.builder()
        .id(property.getId())
        .createdAt(property.getCreatedAt())
        .updatedAt(property.getUpdatedAt())

        .propertyDefault(propertyDefaultResp)
        .propertyPrice(propertyPriceResp)
        .propertyLedge(propertyLedgeResps)
        .propertyFloor(propertyFloorResp)
        .propertyLand(propertyLandResps)
        .propertyAddressResp(addressResp)
        .details(detailsResp)
        .admin(propertyAdminResp)
        .sale(saleResp)
        .propertyTemplate(templateResp)
        .build();
  }

  public PropertyStatResp getStat(PropertyStatReq req) {
    return propertyRepository.getStat(req);
  }

  @Transactional
  public Boolean togglePublic(Long propertyId) {
    Property property = getById(propertyId);

    Boolean newStatus = !property.getIsPublic();
    property.setIsPublic(newStatus);

    return newStatus;
  }

  public List<Property> getsByMap(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    return propertyRepository.getsByMap(req, adminIds, bigCategoryIds, smallCategoryIds);
  }

  public List<PropertyAdminMapDto> getsByMapDto(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    return propertyRepository.getsByMapDto(req, adminIds, bigCategoryIds, smallCategoryIds);
  }

  public List<Property> gets(List<Long> ids) {
    return propertyRepository.findAllById(ids);
  }

  public List<Property> gets() {
    return propertyRepository.findAll();
  }

  public List<Property> getList() {
    return propertyRepository.getsOrderByCreatedAt(10);

  }
  @Transactional
  public void deleteById(Long propertyId) {
    Property property = this.getById(propertyId);
    property.setDeletedAt(LocalDateTime.now());
  }

  public List<Property> getsHomeProperty() {
    return propertyRepository.findProperties(PropertyStatus.COMPLETE,8,true);
  }

  public List<Property> getsHomePropertyByMember() {
    return propertyRepository.findPropertiesByMember(PropertyStatus.COMPLETE,8);
  }

  public List<Property> getsHomePropertyByAgent() {
    return propertyRepository.findPropertiesByAgent(8);
  }

  @Transactional
  public void deleteConfirm(Property property) {
    property.setConfirmedAt(null);
    propertyRepository.save(property);
  }

  @Transactional
  public void confirm(Property property) {
    property.setConfirmedAt(LocalDateTime.now());
    propertyRepository.save(property);
  }

  public void saveAll(List<Property> properties) {
    propertyRepository.saveAll(properties);
  }

  public List<Property> getsByMapWithSeller(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds, Member member) {
    return propertyRepository.getsByMapWithSeller(req, adminIds, bigCategoryIds, smallCategoryIds,member);

  }

  public boolean getByexist(Long propertyId) {
    return propertyRepository.findById(propertyId).isPresent();

  }
}
