package com.backend.lab.domain.todayNews.entity.dto;

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
public class TodayNewsSearchOptions extends PageOptions {

  private String query;
}
