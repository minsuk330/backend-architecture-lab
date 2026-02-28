package com.backend.lab.api.admin.property.info.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class PropertySelectedBuildingInfoReq {
  private List<BuildingInfo> buildings;
  private String pnu;
}
