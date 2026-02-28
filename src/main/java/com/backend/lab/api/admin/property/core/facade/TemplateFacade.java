package com.backend.lab.api.admin.property.core.facade;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyTemplateReq;
import com.backend.lab.domain.property.core.entity.embedded.TemplateProperties;
import com.backend.lab.domain.property.core.entity.information.TemplateInformation;
import com.backend.lab.domain.property.core.service.info.TemplateInfoService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemplateFacade {
  private final TemplateInfoService templateInfoService;
  private final UploadFileService uploadFileService;

  @Transactional
  public TemplateInformation createTemplateInformation(PropertyCreateReq req) {
    PropertyTemplateReq templateReq = req.getTemplate();
    if (templateReq == null) {
      return templateInfoService.create(new TemplateProperties());
    }
      // 모든 파일 ID 수집
      List<Long> fileIds = collectFileIds(templateReq);
      Map<Long, UploadFile> fileMap = new HashMap<>(fileIds.size());
      if (!fileIds.isEmpty()) {
        fileMap = uploadFileService.getByIds(fileIds)
            .stream()
            .collect(Collectors.toMap(UploadFile::getId, Function.identity()));
      }

      TemplateProperties props = buildTemplateProperties(templateReq, fileMap);

      return templateInfoService.create(props);

  }

  @Transactional
  public void updateTemplateInformation(PropertyTemplateReq req, Long templateId) {
      List<Long> fileIds = collectFileIds(req);


      Map<Long, UploadFile> fileMap = Collections.emptyMap();
      if (!fileIds.isEmpty()) {
        fileMap = uploadFileService.getByIds(fileIds)
            .stream()
            .collect(Collectors.toMap(UploadFile::getId, Function.identity()));
      }

      setUploadFilesToRequest(req, fileMap);

      templateInfoService.update(req, templateId);


  }

  private List<Long> collectFileIds(PropertyTemplateReq req) {
    return Stream.of(
        req.getInfo1UrlId(), req.getInfo2UrlId(),
        req.getPlanPartUrlId(), req.getPlanEntireUrlId(),
        req.getEtc1UrlId(), req.getEtc2UrlId(), req.getEtc3UrlId(),
        req.getEtc4UrlId(), req.getEtc5UrlId(), req.getEtc6UrlId(),
        req.getEtc7UrlId(), req.getEtc8UrlId(), req.getEtc9UrlId(),
        req.getEtc10UrlId()
    ).filter(Objects::nonNull).toList();
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

  private void setUploadFilesToRequest(PropertyTemplateReq req, Map<Long, UploadFile> fileMap) {
    req.setInfo1Url(getUploadFile(req.getInfo1UrlId(), fileMap));
    req.setInfo2Url(getUploadFile(req.getInfo2UrlId(), fileMap));
    req.setPlanPartUrl(getUploadFile(req.getPlanPartUrlId(), fileMap));
    req.setPlanEntireUrl(getUploadFile(req.getPlanEntireUrlId(), fileMap));
    req.setEtc1Url(getUploadFile(req.getEtc1UrlId(), fileMap));
    req.setEtc2Url(getUploadFile(req.getEtc2UrlId(), fileMap));
    req.setEtc3Url(getUploadFile(req.getEtc3UrlId(), fileMap));
    req.setEtc4Url(getUploadFile(req.getEtc4UrlId(), fileMap));
    req.setEtc5Url(getUploadFile(req.getEtc5UrlId(), fileMap));
    req.setEtc6Url(getUploadFile(req.getEtc6UrlId(), fileMap));
    req.setEtc7Url(getUploadFile(req.getEtc7UrlId(), fileMap));
    req.setEtc8Url(getUploadFile(req.getEtc8UrlId(), fileMap));
    req.setEtc9Url(getUploadFile(req.getEtc9UrlId(), fileMap));
    req.setEtc10Url(getUploadFile(req.getEtc10UrlId(), fileMap));
  }

  private UploadFile getUploadFile(Long fileId, Map<Long, UploadFile> fileMap) {
    if (fileId == null) {
      return null;
    }
    return fileMap.get(fileId);
  }


}
