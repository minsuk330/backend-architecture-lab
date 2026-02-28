package com.backend.lab.common.nice.dto;

import com.backend.lab.common.entity.vo.GenderType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NiceAuthResp {

  private Boolean success;
  private String message;

  private String di;
  private String name;
  private String phoneNumber;
  private LocalDate birth;
  private GenderType gender;

  private String email;
  private String password;

  private String returnUrl;
}
