package com.backend.lab.domain.propertyAdvertisement.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import com.backend.lab.domain.propertyAdvertisement.entity.dto.resp.PropertyAdvertisementResp;
import com.backend.lab.domain.propertyAdvertisement.repository.PropertyAdvertisementRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyAdvertisementService {

  private final PropertyAdvertisementRepository propertyAdvertisementRepository;

  public PropertyAdvertisement getById(Long id) {
    return propertyAdvertisementRepository.findById(id)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "PropertyAdvertisement"));
  }

  public Optional<PropertyAdvertisement> getByPropertyAndAgent(Long propertyId, Long userId) {
    return propertyAdvertisementRepository.findByPropertyIdAndAgentId(propertyId, userId,
        LocalDateTime.now());
  }

  public List<PropertyAdvertisement> getsByProperty(Long propertyId) {
    return propertyAdvertisementRepository.findByPropertyId(propertyId, LocalDateTime.now());
  }

  public PropertyAdvertisement create(Property property, Member member) {
    return propertyAdvertisementRepository.save(
        PropertyAdvertisement.builder()
            .property(property)
            .agent(member)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusMonths(1))
            .build()
    );
  }

  public PropertyAdvertisementResp propertyAdvertisementResp(
      PropertyAdvertisement propertyAdvertisement) {
    return PropertyAdvertisementResp.builder()
        .id(propertyAdvertisement.getId())
        .agentId(propertyAdvertisement.getAgent().getId())
        .name(propertyAdvertisement.getAgent().getAgentProperties().getName())
        .businessName(propertyAdvertisement.getAgent().getAgentProperties().getBusinessName())
        .phoneNumber(propertyAdvertisement.getAgent().getAgentProperties().getPhoneNumber())
        .address(propertyAdvertisement.getAgent().getAgentProperties().getAddress())
        .build();
  }

  @Transactional
  public PropertyAdvertisement createAdvertisement(Property property, Member agent,
      LocalDateTime startDate, LocalDateTime endDate) {
    return propertyAdvertisementRepository.save(
        PropertyAdvertisement.builder()
            .property(property)
            .agent(agent)
            .startDate(startDate)
            .endDate(endDate)
            .build()
    );
  }

  @Transactional
  public void removeAdvertisement(Long propertyId, Long agentId) {
    Optional<PropertyAdvertisement> advertisement =
        propertyAdvertisementRepository.findByPropertyIdAndAgentId(propertyId, agentId,
            LocalDateTime.now());

    if (advertisement.isPresent()) {
      propertyAdvertisementRepository.delete(advertisement.get());
    } else {
      throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND,
          "해당 매물에 대한 공인중개사의 광고를 찾을 수 없습니다");
    }
  }
}