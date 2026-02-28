package com.backend.lab.api.member.agent.property.facade;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestAgentReq;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.mapper.PropertyRequestMapper;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentPropertyFacade {

  private final PropertyService propertyService;
  private final PropertyMapper propertyMapper;
  private final MemberService memberService;
  private final PropertyRequestService propertyRequestService;
  private final UploadFileService uploadFileService;
  private final PropertyRequestMapper propertyRequestMapper;

  //공인중개사 내 매물
  public PageResp<PropertyListResp> getsByExclusiveAgentId(Long userId, PageOptions options) {
    Member member = memberService.getById(userId);
    Page<Property> page = propertyService.getsByExclusiveAgentId(userId, options.pageable());
    return propertyMapper.convertToPageResp(page, member);
  }

  @Transactional
  public void createReq(PropertyRequestAgentReq req, Long memberId) {
    Member member = memberService.getById(memberId);

    PropertyRequest propertyRequest = propertyRequestService.create(req.getPropertyRequestReq(), member,
        RequestType.AGENT);

    UploadFile image = uploadFileService.getById(req.getImageId());

    req.setImage(image);

    propertyRequest.setNonMemberName(req.getName());
    propertyRequest.setNonMemberPhoneNumber(req.getPhoneNumber());
    propertyRequest.setExclusiveContract(req.getImage());
  }


  public PageResp<PropertyRequestAgentResp> getsRequest(Long memberId, PageOptions options) {

    Member member = memberService.getById(memberId);

    Page<PropertyRequest> propertyRequests = propertyRequestService.getsByMember(member,
        options.pageable());

    List<PropertyRequestAgentResp> data = propertyRequests.stream().map(
        propertyRequestMapper::propertyRequestAgentResp
    ).toList();

    return new PageResp<>(propertyRequests, data);
  }


  public PageResp<PropertyListResp> getsByAgentAdv(Long userId, PageOptions options) {
    Member agent = memberService.getById(userId);
    Page<Property> page = propertyService.getsByAgentAdvAll(agent, options.pageable());
    return propertyMapper.convertToPageResp(page, agent);
  }

  public PageResp<PropertySearchResp> search(SearchPropertyOptions options,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {

    Page<Property> search = propertyService.search(options, null, bigCategoryIds,
        smallCategoryIds);
    List<PropertySearchResp> list = search.stream().map(
        propertyMapper::propertySearchResp
    ).toList();

    return new PageResp<>(search, list);

  }
}
