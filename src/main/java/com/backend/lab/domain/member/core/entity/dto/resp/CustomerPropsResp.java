package com.backend.lab.domain.member.core.entity.dto.resp;

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
public class CustomerPropsResp {

  private String name;
  private String phoneNumber;
  private String homePhoneNumber;
  private String etcPhoneNumber;
  private CustomerFunnel funnel;
  private String note;

  private Long adminId; //담당자
  private String adminName;
  private String address; // 주소
  private LocalDate birth; // 생년월일
  private CompanyType companyType;
}
