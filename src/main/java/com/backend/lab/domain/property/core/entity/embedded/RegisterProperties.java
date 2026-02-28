package com.backend.lab.domain.property.core.entity.embedded;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Schema(description = "등기부 등본 정보")
public class RegisterProperties {

  @Schema(description = "발급일")
  private LocalDate issueDate; // 발급일
  @Schema(description = "이름")
  private String name; // 이름
  @Schema(description = "주소")
  private String address; // 주소

  @Schema(description = "갑구")
  private String gabgu; // 갑구
  @Schema(description = "갑구 메모")
  private String gabguMemo; // 갑구 메모

  @Schema(description = "을구")
  private String eulgu; // 을구
  @Schema(description = "을구 메모")
  private String eulguMemo; // 을구 메모

  @Schema(description = "비고")
  @Column(length = 2000)
  private String etc; // 비고

  public RegisterProperties update(RegisterProperties other) {

    if (other == null) {
      return this;
    }

    if (other.issueDate != null) {
      this.issueDate = other.issueDate;
    }
    if (other.name != null) {
      this.name = other.name;
    }
    if (other.address != null) {
      this.address = other.address;
    }
    if (other.gabgu != null) {
      this.gabgu = other.gabgu;
    }
    if (other.gabguMemo != null) {
      this.gabguMemo = other.gabguMemo;
    }
    if (other.eulgu != null) {
      this.eulgu = other.eulgu;
    }
    if (other.eulguMemo != null) {
      this.eulguMemo = other.eulguMemo;
    }
    if (other.etc != null) {
      this.etc = other.etc;
    }
    return this;
  }
}
