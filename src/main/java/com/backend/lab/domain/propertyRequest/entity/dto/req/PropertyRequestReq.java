package com.backend.lab.domain.propertyRequest.entity.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PropertyRequestReq {

  @NotNull
  private String buildingName;
  @NotNull
  private String jibunAddress;
  @NotNull
  private String roadAddress;

  private Integer mmPrice;
  private Integer depositPrice;
  private Integer monthPrice;





}
