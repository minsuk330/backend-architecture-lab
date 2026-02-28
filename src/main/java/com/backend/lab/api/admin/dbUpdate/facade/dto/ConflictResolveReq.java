package com.backend.lab.api.admin.dbUpdate.facade.dto;

import lombok.Getter;

@Getter
public class ConflictResolveReq {

  private String pnu;
  private String roadAddress;
  private String jibunAddress;

  private Double lng;
  private Double lat;
}
