package com.backend.lab.api.admin.property.core.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyStatReq {

  private FilterOptions total;
  private FilterOptions monthly;

  @Getter
  @Setter
  public static class FilterOptions implements StatFilterable {
    private Long departmentId;
    private Long majorCategoryId;
    private Long adminId;
  }

}
