package com.backend.lab.api.member.seller.property.facade;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestSellerReq;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestSellerResp;
import com.backend.lab.api.admin.PropertyRequest.mapper.PropertyRequestMapper;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerPropertyFacade {

  private final PropertyService propertyService;
  private final PropertyMapper propertyMapper;
  private final MemberService memberService;
  private final PropertyRequestService propertyRequestService;
  private final PropertyRequestMapper propertyRequestMapper;

  //매도자 내 매물
 public PageResp<PropertyListResp> getsWithPropertyMember(Long userId,PageOptions options) {
   Member member = memberService.getById(userId);
   Page<Property> page = propertyService.getsWithPropertyMember(userId, options.pageable());
   return propertyMapper.convertToPageResp(page, member);
 }

 @Transactional
  public void createReq(PropertyRequestSellerReq req, Long userId) {

   Member member = memberService.getById(userId);
   propertyRequestService.create(req.getPropertyRequestReq(),member, RequestType.SELLER);
 }

  public PageResp<PropertyRequestSellerResp> getsRequest(Long memberId, PageOptions options) {

    Member member = memberService.getById(memberId);

    Page<PropertyRequest> propertyRequests = propertyRequestService.getsByMember(member,
        options.pageable());

    List<PropertyRequestSellerResp> data = propertyRequests.stream().map(
        propertyRequestMapper::propertyRequestSellerResp
    ).toList();

    return new PageResp<>(propertyRequests, data);
  }
}
