package com.backend.lab.api.admin.property.info.dto.resp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMoveInfoResp {
  private List<PropertyMoveInfoItem> data;
}
