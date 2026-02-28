package com.backend.lab.domain.property.category.entity.dto.resp;

import com.backend.lab.domain.property.category.entity.vo.MenuType;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BigCategoryResp {

  private Long id;
  private Boolean isActive;
  private Integer rank;
  private String name;
  private Set<MenuType> menuTypes;

  private Long migrationCategoryId;
  private String migrationCategoryName;
}
