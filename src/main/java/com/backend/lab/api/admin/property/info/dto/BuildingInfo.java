package com.backend.lab.api.admin.property.info.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingInfo {
  private String buildingName;
  private String jibunAddress;
  private String dongNm;
  private String hoNm;
  private String mgmBldrgstPk;
}
