package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchAgentOptions extends PageOptions {

  private String query;
}
