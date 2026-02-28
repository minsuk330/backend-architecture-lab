package com.backend.lab.api.admin.PropertyRequest.dto.resp;

import com.backend.lab.domain.propertyRequest.entity.dto.resp.PropertyRequestResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class PropertyRequestCustomerResp extends PropertyRequestResp {

  private String name;
  private String phoneNumber;
}
