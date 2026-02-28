package com.backend.lab.domain.property.category.entity.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmallCategoryResp {

  private Long id;
  private Integer rank;
  private String name;
  private Long migrationCategoryId;
  private String migrationCategoryName;
}
