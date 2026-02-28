package com.backend.lab.api.common.searchFilter.dto.resp;

import com.backend.lab.domain.property.core.entity.vo.PropertySortType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PropertySortResp {
  private List<PropertySortType> sortOptions;
  private PropertySortType defaultSort;
}
