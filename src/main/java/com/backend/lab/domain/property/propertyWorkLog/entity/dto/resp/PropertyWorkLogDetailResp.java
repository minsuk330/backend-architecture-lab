package com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.taskNote.entity.vo.WorkLogType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class PropertyWorkLogDetailResp extends BaseResp {
  private LogFieldType fieldType;
  private WorkLogType workLogType;
  private String beforeValue;
  private String afterValue;
  private String adminName;

  public PropertyWorkLogDetailResp(
      Long id,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      LocalDateTime deletedAt,
      LogFieldType fieldType,
      WorkLogType  workLogType,
      String       beforeValue,
      String       afterValue,
      String       adminName) {
    super(id, createdAt, updatedAt,deletedAt);
    this.fieldType   = fieldType;
    this.workLogType = workLogType;
    this.beforeValue = beforeValue;
    this.afterValue  = afterValue;
    this.adminName   = adminName;
  }

}
