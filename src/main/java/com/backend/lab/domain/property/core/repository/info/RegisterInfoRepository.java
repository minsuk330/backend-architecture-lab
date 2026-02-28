package com.backend.lab.domain.property.core.repository.info;

import com.backend.lab.domain.property.core.entity.information.RegisterInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterInfoRepository extends JpaRepository<RegisterInformation, Long> {

}
