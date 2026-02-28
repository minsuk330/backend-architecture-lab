package com.backend.lab.domain.post.entity.dto.req;

import com.backend.lab.domain.post.entity.vo.PostType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostCreateReq {

  @NotNull
  private Boolean canAgentView;
  @NotNull
  private Boolean canBuyerView;
  @NotNull
  private Boolean canSellerView;

  @JsonIgnore
  @Setter
  private PostType type;
  @NotNull
  private String title;
  @NotNull
  private String content;
}
