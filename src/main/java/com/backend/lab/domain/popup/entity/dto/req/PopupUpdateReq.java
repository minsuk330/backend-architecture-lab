package com.backend.lab.domain.popup.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PopupUpdateReq {

  private Boolean canAnonymousView;
  private Boolean canAgentView;
  private Boolean canBuyerView;
  private Boolean canSellerView;

  @Setter
  @JsonIgnore
  private UploadFile image;
  private Long imageId;
  private String url;
  private LocalDate startAt;
  private LocalDate endAt;
}
