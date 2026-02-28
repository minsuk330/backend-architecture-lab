package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.CustomerFunnel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerDTO {

  private String name;
  private GenderType gender;
  private LocalDate birth;
  private String email;
  private String address;
  private String etc;
  private Long adminId;
  private CustomerFunnel funnel;
  private String phoneNumber;
  private String homePhoneNumber;
  private String note;
  private CompanyType companyType;
}
