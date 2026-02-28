package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PropertyDetailResp {

  private PropertyDefaultResp propertyDefault;
  private PropertyLandResp propertyLand;
  private PropertyLedgeResp propertyLedge;
  private PropertyPriceResp propertyPrice;
  private DetailsResp details;
}
