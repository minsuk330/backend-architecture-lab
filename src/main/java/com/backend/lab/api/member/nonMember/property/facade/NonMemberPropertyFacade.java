package com.backend.lab.api.member.nonMember.property.facade;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestNonMemberReq;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.propertyRequest.service.PropertyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NonMemberPropertyFacade {

  private final PropertyRequestService propertyRequestService;

  @Transactional
  public void createReq(PropertyRequestNonMemberReq req) {

    PropertyRequest propertyRequest = propertyRequestService.create(req.getPropertyRequestReq(), null,
        RequestType.NON_MEMBER);

    propertyRequest.setNonMemberName(req.getName());
    propertyRequest.setNonMemberPhoneNumber(req.getPhoneNumber());
  }
}
