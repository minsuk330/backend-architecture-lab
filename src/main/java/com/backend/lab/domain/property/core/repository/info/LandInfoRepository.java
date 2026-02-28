package com.backend.lab.domain.property.core.repository.info;

import com.backend.lab.domain.property.core.entity.information.LandInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandInfoRepository extends JpaRepository<LandInformation, Long> {

}
