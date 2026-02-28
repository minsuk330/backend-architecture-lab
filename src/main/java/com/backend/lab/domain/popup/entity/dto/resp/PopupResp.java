package com.backend.lab.domain.popup.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PopupResp extends BaseResp {

  private Boolean canAnonymousView;
  private Boolean canAgentView;
  private Boolean canBuyerView;
  private Boolean canSellerView;

  @JsonInclude(Include.NON_NULL)
  private AdminResp createdBy;

  private UploadFileResp image;
  private String url;

  private LocalDate startAt; // 팝업 시작일
  private LocalDate endAt; // 팝업 종료일
}
