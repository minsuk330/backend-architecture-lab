package com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.taskNote.entity.vo.WorkLogType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Getter
public class PropertyWorkLogResp extends BaseResp {

  private Long propertyId;
  private WorkLogType workLogType;
  private String buildingName;
  private String address;
  private String createdName;
  private String ip;


  public PropertyWorkLogResp(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt,
      WorkLogType workLogType, String buildingName, String address,
      String createdName, String ip, Long propertyId) {
    super(id, createdAt, updatedAt, deletedAt);  // BaseResp 생성자 호출
    this.workLogType = workLogType;
    this.buildingName = buildingName;
    this.address = address;
    this.createdName = createdName;
    this.ip = ip;
    this.propertyId = propertyId;
  }
}
