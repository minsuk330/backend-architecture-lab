package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.api.admin.property.core.dto.req.PropertyTemplateReq;
import com.backend.lab.domain.property.core.entity.embedded.TemplateProperties;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.repository.info.TemplateInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateInfoService {
  private final TemplateInfoRepository templateInfoRepository;


  @Transactional
  public TemplateInformation create(TemplateProperties templateProperties) {
    return templateInfoRepository.save(
        TemplateInformation.builder()
            .properties(templateProperties)
            .build()
    );
  }

  public TemplateInformation getById(Long id) {
    return templateInfoRepository.findById(id).orElse(null);
  }

  @Transactional
  public void update(PropertyTemplateReq req, Long templateId) {
    TemplateInformation templateInformation = getById(templateId);
    TemplateProperties properties = templateInformation.getProperties();
    
    if (properties == null) {
      properties = new TemplateProperties();
      templateInformation.setProperties(properties);
    }

    // null 체크 제거 - 삭제된 이미지(null)도 반영
    properties.setInfo1Url(req.getInfo1Url());
    properties.setInfo2Url(req.getInfo2Url());
    properties.setPlanPartUrl(req.getPlanPartUrl());
    properties.setPlanEntireUrl(req.getPlanEntireUrl());
    properties.setEtc1Url(req.getEtc1Url());
    properties.setEtc2Url(req.getEtc2Url());
    properties.setEtc3Url(req.getEtc3Url());
    properties.setEtc4Url(req.getEtc4Url());
    properties.setEtc5Url(req.getEtc5Url());
    properties.setEtc6Url(req.getEtc6Url());
    properties.setEtc7Url(req.getEtc7Url());
    properties.setEtc8Url(req.getEtc8Url());
    properties.setEtc9Url(req.getEtc9Url());
    properties.setEtc10Url(req.getEtc10Url());
  }
}
