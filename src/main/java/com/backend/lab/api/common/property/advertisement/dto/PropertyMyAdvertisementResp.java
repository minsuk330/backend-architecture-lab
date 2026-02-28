package com.backend.lab.api.common.property.advertisement.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMyAdvertisementResp {
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private String name;
  private String businessName;
  private String phoneNumber;
  private String address;

}
