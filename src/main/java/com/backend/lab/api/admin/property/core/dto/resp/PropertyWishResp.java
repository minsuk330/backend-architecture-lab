package com.backend.lab.api.admin.property.core.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PropertyWishResp {
  private String groupName;
  private Long wishCount;
}
