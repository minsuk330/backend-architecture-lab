package com.backend.lab.domain.property.core.repository;

import com.backend.lab.domain.property.core.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDeletedRepository extends JpaRepository<Property, Long>,
    PropertyDeletedRepositoryCustom {

}
