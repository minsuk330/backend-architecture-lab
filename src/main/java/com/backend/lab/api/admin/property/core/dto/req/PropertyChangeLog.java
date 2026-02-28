package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyChangeLog {

  private String fieldName;
  private String fieldDisplayName;
  private Object oldValue;
  private Object newValue;
  private TaskType taskType = TaskType.AUTO_UPDATE;
}
