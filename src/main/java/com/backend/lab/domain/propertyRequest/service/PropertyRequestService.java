package com.backend.lab.domain.propertyRequest.service;

import com.backend.lab.api.admin.PropertyRequest.dto.req.SearchPropertyRequestOptions;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.dto.req.PropertyRequestReq;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestStatus;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.propertyRequest.repository.PropertyRequestRepository;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyRequestService {

  private final PropertyRequestRepository propertyRequestRepository;
  private final UploadFileService uploadFileService;

  public Page<PropertyRequest> agentSearch(SearchPropertyRequestOptions options) {
    return propertyRequestRepository.agentSearch(options);
  }

  public Page<PropertyRequest> sellerSearch(SearchPropertyRequestOptions options) {
    return propertyRequestRepository.sellerSearch(options);
  }

  public Page<PropertyRequest> customerSearch(SearchPropertyRequestOptions options) {
    return propertyRequestRepository.customerSearch(options);
  }

  public PropertyRequest getById(Long id) {
    return propertyRequestRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "PropertyRequest not found"));
  }

  public List<PropertyRequest> getsByMember(Member member) {
    return propertyRequestRepository.findAllByRequester(member);
  }

  public Page<PropertyRequest> getsByMember(Member member, Pageable pageable) {
    return propertyRequestRepository.findAllByRequester(member, pageable);
  }

  @Transactional
  public PropertyRequest create(PropertyRequestReq req, Member member, RequestType type) {
    return propertyRequestRepository.save(
        PropertyRequest.builder()
            .requester(member)
            .buildingName(req.getBuildingName())
            .depositPrice(req.getDepositPrice())
            .mmPrice(req.getMmPrice())
            .monthPrice(req.getMonthPrice())
            .jibunAddress(req.getJibunAddress())
            .status(RequestStatus.REQUESTED)
            .roadAddress(req.getRoadAddress())
            .type(type)
            .build()
    );
  }

  @Transactional
  public void approve(Long propertyRequestId, Long adminId) {
    PropertyRequest propertyRequest = getById(propertyRequestId);
    
    if (propertyRequest.getStatus() == RequestStatus.REQUESTED) {
      propertyRequest.setStatus(RequestStatus.APPROVED);
      propertyRequest.setApprovedByAdminId(adminId);
      propertyRequest.setApprovedAt(LocalDateTime.now());
    }

  } 

  @Transactional
  public void reject(Long propertyRequestId) {
    PropertyRequest propertyRequest = getById(propertyRequestId);
    
    if (propertyRequest.getStatus() == RequestStatus.REQUESTED) {
      propertyRequest.setStatus(RequestStatus.REJECTED);
      propertyRequest.setRejectedAt(LocalDateTime.now());
    }
  }





}
