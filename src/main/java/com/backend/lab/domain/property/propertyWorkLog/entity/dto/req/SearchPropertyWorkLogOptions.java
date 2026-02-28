package com.backend.lab.domain.property.propertyWorkLog.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.property.taskNote.entity.vo.WorkLogType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchPropertyWorkLogOptions extends PageOptions {

  private String query;
  private WorkLogType workLogType;

}
