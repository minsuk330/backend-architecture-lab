package com.backend.lab.domain.property.taskNote.entity.dto.req;

import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TaskNoteCreateReq {

  @NotNull
  private TaskType taskType;
  @NotNull
  private String content;
  @Setter
  @JsonIgnore
  private UploadFile image;

  private Long imageId;
  //조회시 넘겨준 값 받아오기

}
