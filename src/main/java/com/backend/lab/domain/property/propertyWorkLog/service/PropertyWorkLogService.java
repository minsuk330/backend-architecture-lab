package com.backend.lab.domain.property.propertyWorkLog.service;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLog;
import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLogDetail;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailsOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.SearchPropertyWorkLogOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogDetailResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogResp;
import com.backend.lab.domain.property.propertyWorkLog.repository.PropertyWorkLogRepository;
import com.backend.lab.domain.property.taskNote.entity.vo.WorkLogType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PropertyWorkLogService {

  private final PropertyWorkLogRepository propertyWorkLogRepository;


  public List<PropertyWorkLog> getByPropertyId(Long propertyId) {
    return propertyWorkLogRepository.findByPropertyOrderByCreatedAtDesc(propertyId);
  }

  public List<PropertyWorkLog> getsByAdminIdTodayBetween(Long adminId, LocalDateTime yesterday,
      LocalDateTime today) {
    return propertyWorkLogRepository.findAllByCreatedByIdAndCreatedAtBetween(adminId, yesterday, today);
  }



  @Transactional
  public void createLog(List<PropertyWorkLogDetail> details, Admin admin, Property property,
      String clientIp) {

    PropertyWorkLog log = PropertyWorkLog.builder()
        .workLogType(WorkLogType.CREATED)
        .details(details)
        .property(property)
        .createdBy(admin)
        .adminIp(clientIp)
        .build();
    propertyWorkLogRepository.save(log);

  }


  //말은 업데이트지만 그냥 새로 만드는것
  @Transactional
  public void updateLog(List<PropertyWorkLogDetail> details, Admin admin, Property property,
      String clientIp) {
    PropertyWorkLog log = PropertyWorkLog.builder()
        .workLogType(WorkLogType.UPDATED)
        .details(details)
        .property(property)
        .createdBy(admin)
        .adminIp(clientIp)
        .build();
    propertyWorkLogRepository.save(log);

  }

  @Transactional
  public void deleteLog(Admin admin, Property property,String clientIp) {
    PropertyWorkLog log = PropertyWorkLog.builder()
        .workLogType(WorkLogType.DELETED)
        .property(property)
        .createdBy(admin)
        .adminIp(clientIp)
        .build();
    propertyWorkLogRepository.save(log);

  }

  public Page<PropertyWorkLogResp> searchLog(SearchPropertyWorkLogOptions options) {
    return propertyWorkLogRepository.search(options);
  }

  public Page<PropertyWorkLogDetailResp> findDetails(PropertyWorkLogDetailOptions options) {
    return propertyWorkLogRepository.getByWorkLogId(options);
  }

  public Page<PropertyWorkLogDetailResp> findAllDetails(PropertyWorkLogDetailsOptions options) {
    return propertyWorkLogRepository.getsByPropertyId(options);
  }



  public PropertyWorkLogDetailResp propertyWorkLogDetailResp(PropertyWorkLogDetail detail) {
    return PropertyWorkLogDetailResp.builder()
        .beforeValue(detail.getBeforeValue())
        .afterValue(detail.getAfterValue())
        .fieldType(detail.getLogFieldType())
        .build();
  }

  public PropertyWorkLogResp propertyWorkLogResp(PropertyWorkLog log) {
    String adminName =
        log.getCreatedBy().getName() + (log.getCreatedBy().getJobGrade() != null ? " " + log.getCreatedBy().getJobGrade().getName() : "");


    return PropertyWorkLogResp.builder()
        .id(log.getId())
        .createdAt(log.getCreatedAt())
        .updatedAt(log.getUpdatedAt())

        .propertyId(log.getProperty().getId())
        .workLogType(log.getWorkLogType())
        .ip(log.getAdminIp())
        .createdName(adminName)
        .buildingName(log.getProperty().getBuildingName())
        .address(log.getProperty().getAddress().getProperties().getJibunAddress())
        .build();
  }

  public List<PropertyWorkLog> gets() {
    return propertyWorkLogRepository.findByOrderByCreatedAtDesc();
  }

  @Transactional
  public void deleteByProperty(Long propertyId) {
    List<PropertyWorkLog> propertyWorkLogs = this.getByPropertyId(propertyId);
    propertyWorkLogRepository.deleteAll(propertyWorkLogs);
  }
}
