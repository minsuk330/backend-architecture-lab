package com.backend.lab.domain.propertyMember.service;

import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.repositroy.PropertyMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PropertyMemberService {

  private final PropertyMemberRepository propertyMemberRepository;

  @Transactional
  public void create(Long memberId, Long propertyId) {
    propertyMemberRepository.save(
        PropertyMember.builder()
            .memberId(memberId)
            .propertyId(propertyId)
            .build()
    );
  }

  public List<PropertyMember> getByProperty(Long propertyId) {
    return propertyMemberRepository.findAllByProperty(propertyId);

  }

  public List<PropertyMember> getsByPropertyIds(List<Long> propertyIds) {
    return propertyMemberRepository.findAllByPropertyIdIn(propertyIds);
  }

  public List<PropertyMember> getByMember(Long memberId) {
    return propertyMemberRepository.findAllByMemberId(memberId);
  }
  public PropertyMember getByMemberAndProperty(Long memberId, Long propertyId) {
    return propertyMemberRepository.findByMemberAndProperty(memberId, propertyId).orElse(null);
  }

  @Transactional
  public void createIfNotExists(Long memberId, Long propertyId) {
    if (!existsByMemberIdAndPropertyId(memberId, propertyId)) {
      create(memberId, propertyId);
    }
  }

  public boolean existsByMemberIdAndPropertyId(Long memberId, Long propertyId) {
    // PropertyMember 존재 여부 확인
    return propertyMemberRepository.existsByMemberIdAndPropertyId(memberId, propertyId);
  }

  @Transactional
  public void deleteAll(List<PropertyMember> existingPropertyMembers) {
    propertyMemberRepository.deleteAll(existingPropertyMembers);
  }
}
