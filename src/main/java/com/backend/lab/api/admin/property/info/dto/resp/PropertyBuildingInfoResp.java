package com.backend.lab.api.admin.property.info.dto.resp;

import com.backend.lab.api.admin.property.info.dto.BuildingInfo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyBuildingInfoResp {

  private List<BuildingInfo> buildings;
  private String pnu;
  private int totalCount;

}
