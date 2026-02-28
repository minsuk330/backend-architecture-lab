package com.backend.lab.api.admin.member.agent.facade.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAgentSimpleResp {

  @Schema(description = "공인중개사 ID")
  private Long id;

  @Schema(description = "이메일")
  private String email;

  @Schema(description = "이름")
  private String name;

  @Schema(description = "상호명")
  private String businessName;

  @Schema(description = "전화번호")
  private String phoneNumber;

  @Schema(description = "주소")
  private String address;

  @Schema(description = "상세주소")
  private String addressDetail;
}
