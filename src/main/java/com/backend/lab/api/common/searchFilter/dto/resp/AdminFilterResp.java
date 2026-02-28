package com.backend.lab.api.common.searchFilter.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminFilterResp {
  private Long id;
  private String name;//직급도 포함?
}
