package com.backend.lab.domain.property.core.repository.info;

import com.backend.lab.domain.property.core.entity.information.PriceInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceInfoRepository extends JpaRepository<PriceInformation, Long> {

}
