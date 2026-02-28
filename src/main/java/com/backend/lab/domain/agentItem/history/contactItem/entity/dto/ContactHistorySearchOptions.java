package com.backend.lab.domain.agentItem.history.contactItem.entity.dto;

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
public class ContactHistorySearchOptions extends PageOptions {

  private String query;
  private Long agentId;
  private LocalDate day;
}
