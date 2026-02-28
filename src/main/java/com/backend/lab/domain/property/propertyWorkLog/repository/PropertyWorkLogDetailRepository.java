package com.backend.lab.domain.property.propertyWorkLog.repository;

import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLogDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyWorkLogDetailRepository extends JpaRepository<PropertyWorkLogDetail, Long> {
}
