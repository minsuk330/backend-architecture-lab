package com.backend.lab.domain.post.entity.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PostUpdateReq {

  @NotNull
  private Boolean canAgentView;
  @NotNull
  private Boolean canBuyerView;
  @NotNull
  private Boolean canSellerView;

  @NotNull
  private String title;
  @NotNull
  private String content;

}
