package com.backend.lab.api.admin.property.info.dto.resp;

import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyLandInfoResp {

  private LandProperties land;

}
