package com.backend.lab.api.admin.member.agent.facade.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminAgentListOptions extends PageOptions {

  @Schema(description = "검색어 (이름, 상호명, 전화번호, 이메일)", example = "홍길동")
  private String query;
}
