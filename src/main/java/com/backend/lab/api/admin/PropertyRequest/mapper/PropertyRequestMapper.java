package com.backend.lab.api.admin.PropertyRequest.mapper;

import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestCustomerResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestSellerResp;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PropertyRequestMapper {

  private final UploadFileService uploadFileService;

  public PropertyRequestAgentResp propertyRequestAgentResp(PropertyRequest propertyRequest) {
    if (propertyRequest == null) {
      return null;
    }
    UploadFile exclusiveContract = propertyRequest.getExclusiveContract();

    PropertyRequestAgentResp.PropertyRequestAgentRespBuilder builder = PropertyRequestAgentResp.builder()
        .id(propertyRequest.getId())
        .createdAt(propertyRequest.getCreatedAt())
        .updatedAt(propertyRequest.getUpdatedAt())

        .buildingName(propertyRequest.getBuildingName())
        .jibunAddress(propertyRequest.getJibunAddress())
        .roadAddress(propertyRequest.getRoadAddress())
        .mmPrice(propertyRequest.getMmPrice())
        .depositPrice(propertyRequest.getDepositPrice())
        .monthPrice(propertyRequest.getMonthPrice())
        .status(propertyRequest.getStatus())
        .approvedAt(propertyRequest.getApprovedAt())
        .rejectedAt(propertyRequest.getRejectedAt());

    if (propertyRequest.getRequester() != null &&
        propertyRequest.getRequester().getType() == MemberType.AGENT) {

      builder.name(propertyRequest.getNonMemberName())
          .agentId(propertyRequest.getRequester() != null ?
              propertyRequest.getRequester().getId() : null)
          .phoneNumber(propertyRequest.getNonMemberPhoneNumber())
          .agentName(propertyRequest.getRequester().getAgentProperties() != null ?
              propertyRequest.getRequester().getAgentProperties().getName() : null)
          .businessName(propertyRequest.getRequester().getAgentProperties() != null ?
              propertyRequest.getRequester().getAgentProperties().getBusinessName() : null);

      // 전속계약서 URL
      if (exclusiveContract != null) {
        builder.contract(uploadFileService.uploadFileResp(exclusiveContract));
      }
    }

    return builder.build();
  }

  public PropertyRequestSellerResp propertyRequestSellerResp(PropertyRequest propertyRequest) {
    if (propertyRequest == null) {
      return null;
    }

    PropertyRequestSellerResp.PropertyRequestSellerRespBuilder builder = PropertyRequestSellerResp.builder()
        .id(propertyRequest.getId())
        .createdAt(propertyRequest.getCreatedAt())
        .updatedAt(propertyRequest.getUpdatedAt())

        .buildingName(propertyRequest.getBuildingName())
        .jibunAddress(propertyRequest.getJibunAddress())
        .roadAddress(propertyRequest.getRoadAddress())
        .mmPrice(propertyRequest.getMmPrice())
        .depositPrice(propertyRequest.getDepositPrice())
        .monthPrice(propertyRequest.getMonthPrice())
        .status(propertyRequest.getStatus())
        .approvedAt(propertyRequest.getApprovedAt())
        .rejectedAt(propertyRequest.getRejectedAt());

    // 매도자 정보 (SELLER 타입인 경우)
    if (propertyRequest.getRequester() != null &&
        propertyRequest.getRequester().getType() == MemberType.SELLER) {

      builder.sellerName(propertyRequest.getRequester().getSellerProperties() != null ?
              propertyRequest.getRequester().getSellerProperties().getName() : null)
          .sellerId(propertyRequest.getRequester() != null ?
              propertyRequest.getRequester().getId() : null)
          .phoneNumber(propertyRequest.getRequester().getSellerProperties() != null ?
              propertyRequest.getRequester().getSellerProperties().getPhoneNumber() : null)
          .companyType(propertyRequest.getRequester().getSellerProperties() != null ?
              propertyRequest.getRequester().getSellerProperties().getCompanyType() : null);
    }

    return builder.build();
  }

  public PropertyRequestCustomerResp propertyRequestCustomerResp(PropertyRequest propertyRequest) {
    if (propertyRequest == null) {
      return null;
    }


    PropertyRequestCustomerResp.PropertyRequestCustomerRespBuilder builder = PropertyRequestCustomerResp.builder()
        .id(propertyRequest.getId())
        .createdAt(propertyRequest.getCreatedAt())
        .updatedAt(propertyRequest.getUpdatedAt())

        .buildingName(propertyRequest.getBuildingName())
        .jibunAddress(propertyRequest.getJibunAddress())
        .roadAddress(propertyRequest.getRoadAddress())
        .mmPrice(propertyRequest.getMmPrice())
        .depositPrice(propertyRequest.getDepositPrice())
        .monthPrice(propertyRequest.getMonthPrice())
        .status(propertyRequest.getStatus())
        .approvedAt(propertyRequest.getApprovedAt())
        .rejectedAt(propertyRequest.getRejectedAt());

    if (propertyRequest.getType() == RequestType.NON_MEMBER) {

      builder
          .name(propertyRequest.getNonMemberName() != null ?
              propertyRequest.getNonMemberName() : null)
          .phoneNumber(propertyRequest.getNonMemberPhoneNumber() != null ?
              propertyRequest.getNonMemberPhoneNumber() : null);
    }

    return builder.build();
  }
}
