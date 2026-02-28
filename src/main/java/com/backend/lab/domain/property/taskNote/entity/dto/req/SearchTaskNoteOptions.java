package com.backend.lab.domain.property.taskNote.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchTaskNoteOptions extends PageOptions {

  private String query;
  private TaskType taskType;
  private Long adminId;

}
