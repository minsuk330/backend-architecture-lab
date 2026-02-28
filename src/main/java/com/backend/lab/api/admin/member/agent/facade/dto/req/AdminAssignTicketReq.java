package com.backend.lab.api.admin.member.agent.facade.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAssignTicketReq {

  @Schema(description = "이용권 이름", example = "베이직 30일 이용권")
  @NotBlank(message = "이용권 이름은 필수입니다")
  private String ticketName;

  @Schema(description = "이용권 시작일", example = "2025-10-12T00:00:00")
  @NotNull(message = "이용권 시작일은 필수입니다")
  private LocalDateTime ticketStartAt;

  @Schema(description = "이용권 종료일", example = "2025-11-12T23:59:59")
  @NotNull(message = "이용권 종료일은 필수입니다")
  private LocalDateTime ticketEndAt;
}
