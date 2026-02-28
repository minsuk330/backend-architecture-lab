package com.backend.lab.domain.secret.service;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.secret.entity.Secret;
import com.backend.lab.domain.secret.entity.dto.req.SecretReq;
import com.backend.lab.domain.secret.entity.dto.resp.SecretResp;
import com.backend.lab.domain.secret.repository.SecretRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecretService {
  private final SecretRepository secretRepository;

  public Secret getByPropertyAndAdmin(Long propertyId, Long adminId) {
    return secretRepository.findByPropertyAndCreatedBy(propertyId, adminId).orElse(null);
  }

  public List<Secret> getsByProperty(Long propertyId) {
    return secretRepository.findAllByPropertyId(propertyId);
  }

  public List<Secret> getsByPropertyIds(List<Long> propertyIds) {
    return secretRepository.findAllByPropertyIdIn(propertyIds);
  }

  public List<Secret> filter(Long adminId, Department department, SecretMemoExposeLevel level, List<Secret> original) {

    if (level == null || original == null || original.isEmpty()) {
      return List.of();
    }

    return switch (level) {
      case ME -> original.stream()
          .filter(s -> s != null && s.getCreatedBy() != null && 
                      s.getCreatedBy().getId() != null && 
                      s.getCreatedBy().getId().equals(adminId))
          .toList();
      case DEPARTMENT -> Optional.ofNullable(department)
          .map(dept -> original.stream()
              .filter(s -> s != null && s.getCreatedBy() != null &&
                          Optional.ofNullable(s.getCreatedBy().getDepartment())
                              .map(Department::getId)
                              .map(id -> Objects.equals(id, dept.getId()))
                              .orElse(false))
              .toList())
          .orElse(Collections.emptyList());
      case ALL -> original.stream()
          .filter(s -> s != null && s.getCreatedBy() != null)
          .toList();
    };
  }

  @Transactional
  public void create(SecretReq req, Property property, Admin admin) {

    if (req.getContent()==null||req.getContent().isEmpty()) {
      return;
    }

    Optional<Secret> secret = secretRepository.findByPropertyAndCreatedBy(property.getId(), admin.getId());

    if(secret.isPresent()) {
      secret.get().setContent(req.getContent());
    }
    else {
      secretRepository.save(
          Secret.builder()
              .property(property)
              .content(req.getContent())
              .createdBy(admin)
              .build()
      );
    }
  }

  @Transactional
  public void update(SecretReq req, Admin admin,  Long propertyId) {
    Secret secret = getByPropertyAndAdmin(propertyId, admin.getId());

    secret.setContent(req.getContent());
  }

  public SecretResp secretResp(Secret secret) {
    if (secret == null) {
      return null;
    }
    
    return SecretResp.builder()
        .id(secret.getId())
        .createdAt(secret.getCreatedAt())
        .updatedAt(secret.getUpdatedAt())

        .createdId(secret.getCreatedBy().getId())
        .content(secret.getContent())
        .createdName(secret.getCreatedBy() != null ? secret.getCreatedBy().getName() : null)
        .build();
  }

  @Transactional
  public void delete(Long propertyId, Long adminId) {
    Secret secret = secretRepository.findByPropertyAndCreatedBy(
        propertyId, adminId).get();
    secretRepository.delete(secret);
  }
  
  @Transactional
  public void deleteByProperty(Long propertyId) {
    List<Secret> secrets = getsByProperty(propertyId);
    secretRepository.deleteAll(secrets);
  }
}
