package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.details.entity.dto.resp.DetailsResp;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PropertyResp extends BaseResp {

  private PropertyAdminResp admin;
  private PropertyDefaultResp propertyDefault;
  private PropertyPriceResp propertyPrice;
  private List<PropertyLedgeResp> propertyLedge;
  private PropertyFloorResp propertyFloor;
  private List<PropertyLandResp> propertyLand;
  private DetailsResp details;
  private SaleResp sale;
  private PropertyAddressResp propertyAddressResp;
  private PropertyTemplateResp propertyTemplate;

}
