package com.backend.lab.api.common.property.advertisement.dto;

import com.backend.lab.domain.propertyAdvertisement.entity.dto.resp.PropertyAdvertisementResp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyAdvResp {

  private Boolean isExclusive;
  private Boolean isMyExclusive;

  private PropertyMyAdvertisementResp my;
  private List<PropertyAdvertisementResp> agents;


}
