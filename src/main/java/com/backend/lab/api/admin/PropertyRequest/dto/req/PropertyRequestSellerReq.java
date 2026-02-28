package com.backend.lab.api.admin.PropertyRequest.dto.req;

import com.backend.lab.domain.propertyRequest.entity.dto.req.PropertyRequestReq;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;

@Getter
public class PropertyRequestSellerReq {

  @JsonUnwrapped
  private PropertyRequestReq propertyRequestReq;

}
