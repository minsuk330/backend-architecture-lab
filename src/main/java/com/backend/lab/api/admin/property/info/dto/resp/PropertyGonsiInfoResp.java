package com.backend.lab.api.admin.property.info.dto.resp;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PropertyGonsiInfoResp {
  private List<PropertyGonsiInfo> data;
}
