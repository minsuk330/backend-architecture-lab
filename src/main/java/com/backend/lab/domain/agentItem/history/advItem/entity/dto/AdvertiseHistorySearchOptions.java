package com.backend.lab.domain.agentItem.history.advItem.entity.dto;

import com.backend.lab.common.entity.dto.req.PageOptions;
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
public class AdvertiseHistorySearchOptions extends PageOptions {

  private String query;
}
