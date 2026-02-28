package com.backend.lab.api.common.searchFilter.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MinerCategoryFilterResp {

  private Long id;
  private String name;

}
