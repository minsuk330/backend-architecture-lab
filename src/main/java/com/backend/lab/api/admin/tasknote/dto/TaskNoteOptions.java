package com.backend.lab.api.admin.tasknote.dto;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ParameterObject
public class TaskNoteOptions extends PageOptions {
  private Long propertyId;
}
