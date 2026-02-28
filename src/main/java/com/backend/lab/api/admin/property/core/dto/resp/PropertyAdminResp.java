package com.backend.lab.api.admin.property.core.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyAdminResp {

  private String name;
  private String email;
  private String phoneNumber;
  private String jobGrade;
}
