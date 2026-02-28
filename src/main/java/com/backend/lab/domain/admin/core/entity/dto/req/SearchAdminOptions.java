package com.backend.lab.domain.admin.core.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchAdminOptions extends PageOptions {
  private AdminRole role;
  private Long departmentId;
  private Long jobGradeId;
  private String query;
}
