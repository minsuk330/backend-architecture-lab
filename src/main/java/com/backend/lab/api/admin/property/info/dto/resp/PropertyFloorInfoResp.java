package com.backend.lab.api.admin.property.info.dto.resp;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFloorInfoResp {

  private List<PropertyFloorInfoItem> data;
}
