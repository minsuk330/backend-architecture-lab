package com.backend.lab.api.member.common.property.core.facade;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.details.service.DetailsService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.service.SaleService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import com.backend.lab.domain.wishlist.service.WishlistService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberPropertyFacade {

  private final PropertyService propertyService;
  private final MemberService memberService;
  private final DetailsService detailsService;
  private final UploadFileService uploadFileService;
  private final PropertyMapper propertyMapper;
  private final SaleService saleService;
  private final WishlistService wishlistService;

  public ListResp<PropertyListResp> getsByMap(PropertyMapListReq req, Long userIdWithAnonymous,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {

    // 비회원 처리
    //매물 완료 + 공개인 경우만
    if (userIdWithAnonymous == null) {
      return handleNonMember(req, bigCategoryIds, smallCategoryIds);
    }

    // 회원 처리
    Member member = memberService.getById(userIdWithAnonymous);
    return handleMember(req, bigCategoryIds, smallCategoryIds, member);

  }

  public ListResp<PropertyListResp> gets(Long userIdWithAnonymous) {
    //비회원 처리
    if (userIdWithAnonymous == null) {
      List<Property> properties = propertyService.getsHomeProperty();
      List<PropertyListResp> list = properties.stream().map(propertyMapper::propertyListResp)
          .toList();

      return new ListResp<>(list);
    }
    Member member = memberService.getById(userIdWithAnonymous);

    //공인중개사 처리
    if (member.getType() == MemberType.AGENT) {
      List<Property> properties = propertyService.getsHomePropertyByAgent();
      List<PropertyListResp> list = properties.stream().map(propertyMapper::propertyListResp)
          .toList();
      return new ListResp<>(list);
    }
    //회원 처리
    List<Property> properties = propertyService.getsHomePropertyByMember();
    List<PropertyListResp> list = properties.stream().map(propertyMapper::propertyListResp)
        .toList();

    return new ListResp<>(list);
  }

  private ListResp<PropertyListResp> handleNonMember(PropertyMapListReq req,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    //매물 완료 + 공개인 경우만
    req.setIsPublic(true);
    req.setStatus(PropertyStatus.COMPLETE);

    List<Property> list = propertyService.getsByMap(req, null, bigCategoryIds,
        smallCategoryIds);
    List<PropertyListResp> data = list.stream().map(
        propertyMapper::propertyListResp
    ).toList();

    return new ListResp<>(data);
  }

  private ListResp<PropertyListResp> handleMember(PropertyMapListReq req, List<Long> bigCategoryIds,
      List<Long> smallCategoryIds, Member member) {
    MemberType type = member.getType();

    ListResp<PropertyListResp> resp = switch (type) {
      case AGENT -> handleAgent(req, bigCategoryIds, smallCategoryIds, member);
      case SELLER -> handleSeller(req, bigCategoryIds, smallCategoryIds, member);
      case BUYER -> handleBuyer(req, bigCategoryIds, smallCategoryIds, member);
      default -> throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "지원하지 않는 회원 타입입니다.");
    };

    List<PropertyListResp> data = resp.getData();

    List<Long> propertyIds = data.stream().map(PropertyListResp::getId).toList();
    Set<Long> wishlistPropertyIds = wishlistService.getWishlistPropertyIds(member.getId(), propertyIds);
    
    List<PropertyListResp> withWish = data.stream()
        .peek(p -> p.setIsMywish(wishlistPropertyIds.contains(p.getId())))
        .toList();
    return new ListResp<>(withWish);
  }

  private ListResp<PropertyListResp> handleAgent(PropertyMapListReq req, List<Long> bigCategoryIds,
      List<Long> smallCategoryIds,
      Member member) {
    //모든 매물이 다 보임
    List<Property> list = propertyService.getsByMap(req, null, bigCategoryIds,
        smallCategoryIds);
    List<PropertyListResp> data = list.stream().map(
            propertyMapper::propertyListResp
        )
        .toList();

    return new ListResp<>(data);
  }

  private ListResp<PropertyListResp> handleSeller(PropertyMapListReq req, List<Long> bigCategoryIds,
      List<Long> smallCategoryIds, Member member) {

    //자신의 매물은 봐야 한다.
    //todo
    List<Property> list = propertyService.getsByMapWithSeller(req, null, bigCategoryIds,
        smallCategoryIds, member);
    List<PropertyListResp> data = list.stream().map(
        propertyMapper::propertyListResp
    ).toList();

    return new ListResp<>(data);
  }

  private ListResp<PropertyListResp> handleBuyer(PropertyMapListReq req, List<Long> bigCategoryIds,
      List<Long> smallCategoryIds,
      Member member) {
    //상태 완료인 매물만 보인다.
    req.setStatus(PropertyStatus.COMPLETE);
    List<Property> list = propertyService.getsByMap(req, null, bigCategoryIds,
        smallCategoryIds);
    List<PropertyListResp> data = list.stream().map(
        propertyMapper::propertyListResp
    ).toList();

    return new ListResp<>(data);
  }

  //공인중개사의 경우에만 매도자 정보 구매해서 볼 수 있음
  //다른 맴버의 경우는 매도자 정보 X
  public PropertyResp get(Long propertyId, Long userId) {
    Property property = propertyService.getByIdWithAllDetails(propertyId);
    Member member = null;
    if (userId != null) {
      member = memberService.getById(userId);
    }

    Property byIdWithFloors = propertyService.getByIdWithFloors(propertyId);

    Details details = detailsService.getByPropertyId(propertyId);

    List<UploadFileResp> propertyImages = details.getPropertyImages().stream().map(
        uploadFileService::uploadFileResp
    ).toList();

    List<UploadFileResp> dataImages = details.getDataImages().stream().map(
        uploadFileService::uploadFileResp
    ).toList();
    Sale sale = saleService.getByProperty(propertyId);

    return propertyService.propertyResp(property,
        propertyMapper.propertyAdminResp(property),
        propertyMapper.propertyDefaultResp(property, member),
        propertyMapper.propertyPriceResp(property),
        propertyMapper.propertyLedgeResp(property),
        propertyMapper.toPropertyFloorResp(byIdWithFloors),
        propertyMapper.propertyLandResp(property),
        detailsService.detailsResp(details, propertyImages, dataImages),
        propertyMapper.propertyAddressResp(property),
        sale != null ? saleService.saleResp(sale) : null,
        propertyMapper.propertyTemplateResp(property)
    );

  }


}
