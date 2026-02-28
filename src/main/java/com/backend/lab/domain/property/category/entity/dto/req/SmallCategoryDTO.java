package com.backend.lab.domain.property.category.entity.dto.req;

import lombok.Getter;

@Getter
public class SmallCategoryDTO {

  private Integer rank;
  private String name;
  private Long migrationCategoryId;
}
