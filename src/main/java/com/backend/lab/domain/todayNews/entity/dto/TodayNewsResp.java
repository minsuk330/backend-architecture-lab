package com.backend.lab.domain.todayNews.entity.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TodayNewsResp extends BaseResp {

  @JsonInclude(Include.NON_NULL)
  private AdminResp createdBy;

  private String title;
  private String company;
  private String url;
}
