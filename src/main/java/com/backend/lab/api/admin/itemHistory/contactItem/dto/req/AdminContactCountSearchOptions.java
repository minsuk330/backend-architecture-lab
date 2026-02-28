package com.backend.lab.api.admin.itemHistory.contactItem.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminContactCountSearchOptions extends PageOptions {

  private LocalDate day;
  private String query;
}
