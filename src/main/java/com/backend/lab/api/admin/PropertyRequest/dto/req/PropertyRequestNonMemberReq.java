package com.backend.lab.api.admin.PropertyRequest.dto.req;

import com.backend.lab.domain.propertyRequest.entity.dto.req.PropertyRequestReq;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PropertyRequestNonMemberReq {
  @JsonUnwrapped
  private PropertyRequestReq propertyRequestReq;
  @NotNull
  private String name;
  @NotNull
  private String phoneNumber;

}
