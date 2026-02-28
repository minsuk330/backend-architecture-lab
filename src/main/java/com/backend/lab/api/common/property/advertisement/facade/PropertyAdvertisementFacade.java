package com.backend.lab.api.common.property.advertisement.facade;

import com.backend.lab.api.common.property.advertisement.dto.PropertyAdvResp;
import com.backend.lab.api.common.property.advertisement.dto.PropertyMyAdvertisementResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import com.backend.lab.domain.propertyAdvertisement.entity.dto.resp.PropertyAdvertisementResp;
import com.backend.lab.domain.propertyAdvertisement.service.PropertyAdvertisementService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyAdvertisementFacade {

  private final PropertyAdvertisementService propertyAdvertisementService;
  private final MemberService memberService;
  private final PropertyService propertyService;
  private final UploadFileService uploadFileService;


  public PropertyAdvResp getPropertyAgent(Long propertyId, Long userId) {
    List<PropertyAdvertisement> propertyAdvertisements = propertyAdvertisementService.getsByProperty(
        propertyId);

    Member member = null;
    List<PropertyAdvertisementResp> agents = null;
    if (userId != null) {
      member = memberService.getById(userId);
    }

    PropertyMyAdvertisementResp my = null;
    Property property = propertyService.getById(propertyId);

    // 전속 매물만 걸릴때
    Long exclusiveAgentId = property.getExclusiveAgentId();
    if (exclusiveAgentId != null) {

      if (member != null && exclusiveAgentId.equals(member.getId())) {
        AgentProperties agentProperties = member.getAgentProperties();
        my = PropertyMyAdvertisementResp.builder()
            .startDate(null)
            .endDate(null)
            .name(agentProperties.getName())
            .businessName(agentProperties.getBusinessName())
            .phoneNumber(agentProperties.getPhoneNumber())
            .address(agentProperties != null ? agentProperties.getAddress() : null)
            .build();
      } else {
        Member agent = memberService.getById(exclusiveAgentId);
        AgentProperties agentProperties = agent.getAgentProperties();
        agents = List.of(
            PropertyAdvertisementResp.builder()
                .id(null)
                .agentId(agent.getId())
                .profileImage(uploadFileService.uploadFileResp(agent.getProfileImage()))
                .name(agentProperties.getName())
                .businessName(agentProperties.getBusinessName())
                .phoneNumber(agentProperties.getPhoneNumber())
                .address(agentProperties != null ? agentProperties.getAddress() : null)
                .build()
        );
      }

      return PropertyAdvResp.builder()
          .isExclusive(true)
          .isMyExclusive(my != null)
          .my(my)
          .agents(agents)
          .build();
    }

    if (propertyAdvertisements.isEmpty()) {
      return PropertyAdvResp.builder()
          .isExclusive(false)
          .isMyExclusive(false)
          .my(null)
          .agents(Collections.emptyList())
          .build();
    }

    agents = propertyAdvertisements.stream()
        .map(this::propertyAdvertisementResp)
        .toList();

    // 내 광고 찾기
    if (member != null && member.getType().equals(MemberType.AGENT)) {
      PropertyAdvertisement myAd = propertyAdvertisements.stream()
          .filter(ad -> ad.getAgent().getId().equals(userId))
          .findFirst()
          .orElse(null);

      agents = propertyAdvertisements.stream()
          .filter(ad -> !ad.getAgent().getId().equals(userId))
          .map(this::propertyAdvertisementResp)
          .toList();

      if (myAd != null) {
        my = propertyMyAdvertisementResp(myAd);
      }
    }

    // 다른 에이전트들의 광고 내 광고는 제외하고

    return PropertyAdvResp.builder()
        .isExclusive(false)
        .isMyExclusive(false)
        .my(my)
        .agents(agents)
        .build();
  }

  private PropertyMyAdvertisementResp propertyMyAdvertisementResp(PropertyAdvertisement ad) {
    Member agent = ad.getAgent();
    AgentProperties agentProps = agent.getAgentProperties();

    return PropertyMyAdvertisementResp.builder()
        .startDate((ad.getStartDate()))
        .endDate((ad.getEndDate()))
        .name(agentProps.getName())
        .businessName(agentProps.getBusinessName())
        .phoneNumber(agentProps.getPhoneNumber())
        .address(agentProps.getAddress())
        .build();
  }

  private PropertyAdvertisementResp propertyAdvertisementResp(PropertyAdvertisement ad) {
    Member agent = ad.getAgent();
    AgentProperties agentProps = agent.getAgentProperties();

    return PropertyAdvertisementResp.builder()
        .id(ad.getId())
        .agentId(agent.getId())
        .profileImage(uploadFileService.uploadFileResp(agent.getProfileImage()))
        .name(agentProps.getName())
        .businessName(agentProps.getBusinessName())
        .phoneNumber(agentProps.getPhoneNumber())
        .address(agentProps.getAddress())
        .build();
  }
}
