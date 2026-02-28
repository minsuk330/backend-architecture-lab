package com.backend.lab.domain.post.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.post.entity.vo.PostType;
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
public class PostResp extends BaseResp {

  @JsonInclude(Include.NON_NULL)
  private AdminResp createdBy;

  private Boolean canAgentView; // 에이전트가 볼 수 있는지 여부
  private Boolean canBuyerView; // 매수자가 볼 수 있는지 여부
  private Boolean canSellerView; // 매도자가 볼 수 있는지 여부

  private PostType type;
  private String title;
  private String content;
}
