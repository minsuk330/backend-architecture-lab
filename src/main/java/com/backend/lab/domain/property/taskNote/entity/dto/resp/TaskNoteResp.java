package com.backend.lab.domain.property.taskNote.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TaskNoteResp extends BaseResp {
   private String content;
   private Long createdId;
   private boolean propertyDeleted;
   private Long propertyId;
   private UploadFileResp image;
   private String createdBy;
   private TaskType taskType;
   private String before;
   private String after;
}
