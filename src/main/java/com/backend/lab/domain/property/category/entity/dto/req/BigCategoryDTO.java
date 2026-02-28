package com.backend.lab.domain.property.category.entity.dto.req;

import com.backend.lab.domain.property.category.entity.vo.MenuType;
import java.util.Set;
import lombok.Getter;

@Getter
public class BigCategoryDTO {

  private Boolean isActive;

  private Integer rank;
  private String name;
  private Set<MenuType> menuTypes;

  private Long migrationCategoryId;
}
