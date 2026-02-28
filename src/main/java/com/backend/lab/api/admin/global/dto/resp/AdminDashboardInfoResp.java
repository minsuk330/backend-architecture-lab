package com.backend.lab.api.admin.global.dto.resp;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardInfoResp {

  @JsonUnwrapped
  private AdminGlobalResp admin;
  private boolean isActive;  // 접속 여부
  private PropertyCount property;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PropertyCount {

    private Long total;
    private Long registerYesterday;
    private Long registerToday;
    private Long updateYesterday;
    private Long updateToday;
    private Long deleteYesterday;
    private Long deleteToday;
  }
}
