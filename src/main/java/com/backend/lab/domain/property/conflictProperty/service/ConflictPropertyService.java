package com.backend.lab.domain.property.conflictProperty.service;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.property.conflictProperty.entity.ConflictProperty;
import com.backend.lab.domain.property.conflictProperty.repository.ConflictPropertyRepository;
import com.backend.lab.domain.property.core.entity.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConflictPropertyService {

  private final ConflictPropertyRepository conflictPropertyRepository;

  public Page<ConflictProperty> gets(PageOptions options) {
    return conflictPropertyRepository.findAll(options.pageable());
  }

  public Optional<ConflictProperty> getByProperty(Property property) {
    return conflictPropertyRepository.findByProperty(property);
  }

  public boolean existsAny() {
    return conflictPropertyRepository.count() > 0;
  }

  @Transactional
  public void create(List<Property> conflicts) {
    List<ConflictProperty> rows = new ArrayList<>();
    for (Property property : conflicts) {
      rows.add(ConflictProperty.builder()
          .property(property)
          .build());
    }

    conflictPropertyRepository.saveAll(rows);
  }

  @Transactional
  public void delete(ConflictProperty property) {
    conflictPropertyRepository.delete(property);
  }
}
