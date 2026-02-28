package com.backend.lab.api.admin.PropertyRequest.dto.req;

import com.backend.lab.domain.propertyRequest.entity.dto.req.PropertyRequestReq;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PropertyRequestAgentReq {

  @JsonUnwrapped
  private PropertyRequestReq propertyRequestReq;
  @NotNull
  private String name;
  @NotNull
  private String phoneNumber;
  @NotNull
  private Long imageId;
  @Setter
  @JsonIgnore
  private UploadFile image;



}
