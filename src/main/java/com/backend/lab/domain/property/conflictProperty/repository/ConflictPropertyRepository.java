package com.backend.lab.domain.property.conflictProperty.repository;

import com.backend.lab.domain.property.conflictProperty.entity.ConflictProperty;
import com.backend.lab.domain.property.core.entity.Property;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConflictPropertyRepository extends JpaRepository<ConflictProperty, Long> {

  Optional<ConflictProperty> findByProperty(Property property);
}
