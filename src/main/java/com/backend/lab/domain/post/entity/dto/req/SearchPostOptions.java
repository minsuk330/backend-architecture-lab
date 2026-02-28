package com.backend.lab.domain.post.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.post.entity.vo.PostType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchPostOptions extends PageOptions {

  private String query;
  @Parameter(hidden = true)
  private PostType type;
}
