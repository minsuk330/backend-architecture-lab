package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.api.admin.property.core.dto.req.PropertyTemplateReq;
import com.backend.lab.domain.property.core.entity.embedded.TemplateProperties;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.repository.info.TemplateInfoRepository;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateInfoService {

  private final TemplateInfoRepository templateInfoRepository;
  private final UploadFileService uploadFileService;

  @Transactional
  public TemplateInformation create(PropertyTemplateReq req) {
    if (req == null) {
      return templateInfoRepository.save(
          TemplateInformation.builder()
              .properties(new TemplateProperties())
              .build()
      );
    }

    Map<Long, UploadFile> fileMap = fetchFileMap(req.collectFileIds());
    TemplateProperties props = buildTemplateProperties(req, fileMap);

    return templateInfoRepository.save(
        TemplateInformation.builder()
            .properties(props)
            .build()
    );
  }

  public TemplateInformation getById(Long id) {
    return templateInfoRepository.findById(id).orElse(null);
  }

  @Transactional
  public void update(PropertyTemplateReq req, Long templateId) {
    Map<Long, UploadFile> fileMap = fetchFileMap(req.collectFileIds());

    TemplateInformation templateInformation = getById(templateId);
    TemplateProperties properties = templateInformation.getProperties();

    if (properties == null) {
      properties = new TemplateProperties();
      templateInformation.setProperties(properties);
    }

    properties.setInfo1Url(getUploadFile(req.getInfo1UrlId(), fileMap));
    properties.setInfo2Url(getUploadFile(req.getInfo2UrlId(), fileMap));
    properties.setPlanPartUrl(getUploadFile(req.getPlanPartUrlId(), fileMap));
    properties.setPlanEntireUrl(getUploadFile(req.getPlanEntireUrlId(), fileMap));
    properties.setEtc1Url(getUploadFile(req.getEtc1UrlId(), fileMap));
    properties.setEtc2Url(getUploadFile(req.getEtc2UrlId(), fileMap));
    properties.setEtc3Url(getUploadFile(req.getEtc3UrlId(), fileMap));
    properties.setEtc4Url(getUploadFile(req.getEtc4UrlId(), fileMap));
    properties.setEtc5Url(getUploadFile(req.getEtc5UrlId(), fileMap));
    properties.setEtc6Url(getUploadFile(req.getEtc6UrlId(), fileMap));
    properties.setEtc7Url(getUploadFile(req.getEtc7UrlId(), fileMap));
    properties.setEtc8Url(getUploadFile(req.getEtc8UrlId(), fileMap));
    properties.setEtc9Url(getUploadFile(req.getEtc9UrlId(), fileMap));
    properties.setEtc10Url(getUploadFile(req.getEtc10UrlId(), fileMap));
  }

  private Map<Long, UploadFile> fetchFileMap(List<Long> fileIds) {
    if (fileIds.isEmpty()) {
      return Collections.emptyMap();
    }
    return uploadFileService.getByIds(fileIds)
        .stream()
        .collect(Collectors.toMap(UploadFile::getId, Function.identity()));
  }

  private TemplateProperties buildTemplateProperties(PropertyTemplateReq req, Map<Long, UploadFile> fileMap) {
    return TemplateProperties.builder()
        .info1Url(getUploadFile(req.getInfo1UrlId(), fileMap))
        .info2Url(getUploadFile(req.getInfo2UrlId(), fileMap))
        .planPartUrl(getUploadFile(req.getPlanPartUrlId(), fileMap))
        .planEntireUrl(getUploadFile(req.getPlanEntireUrlId(), fileMap))
        .etc1Url(getUploadFile(req.getEtc1UrlId(), fileMap))
        .etc2Url(getUploadFile(req.getEtc2UrlId(), fileMap))
        .etc3Url(getUploadFile(req.getEtc3UrlId(), fileMap))
        .etc4Url(getUploadFile(req.getEtc4UrlId(), fileMap))
        .etc5Url(getUploadFile(req.getEtc5UrlId(), fileMap))
        .etc6Url(getUploadFile(req.getEtc6UrlId(), fileMap))
        .etc7Url(getUploadFile(req.getEtc7UrlId(), fileMap))
        .etc8Url(getUploadFile(req.getEtc8UrlId(), fileMap))
        .etc9Url(getUploadFile(req.getEtc9UrlId(), fileMap))
        .etc10Url(getUploadFile(req.getEtc10UrlId(), fileMap))
        .build();
  }

  private UploadFile getUploadFile(Long fileId, Map<Long, UploadFile> fileMap) {
    if (fileId == null) {
      return null;
    }
    return fileMap.get(fileId);
  }
}