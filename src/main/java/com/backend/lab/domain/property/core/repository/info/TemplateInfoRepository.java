package com.backend.lab.domain.property.core.repository.info;

import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateInfoRepository extends JpaRepository<TemplateInformation, Long> {

}
