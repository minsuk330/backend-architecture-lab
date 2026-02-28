package com.backend.lab.domain.property.taskNote.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNoteSearchResp extends BaseResp {
  private String content;
  private Long propertyId;
  private boolean propertyDeleted;
  private UploadFileResp fileUrl;
  private String createdBy;
  private TaskType taskType;
  private String before;
  private Long createdId;
  private String after;
  private String buildingName;
  private String address;
}
