package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.property.core.entity.embedded.FloorProperties;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PropertyFloorReq {
  @Setter
  private Set<FloorProperties> floor;
  private Boolean isPublic;

}
