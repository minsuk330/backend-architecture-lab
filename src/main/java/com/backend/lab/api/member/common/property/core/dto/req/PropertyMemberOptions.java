package com.backend.lab.api.member.common.property.core.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.property.category.entity.vo.MenuType;
import com.backend.lab.domain.property.core.entity.vo.PropertySortType;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PropertyMemberOptions extends PageOptions {
  private PropertySortType sortType = PropertySortType.LATEST;
  private List<MenuType> menuTypes;


}
