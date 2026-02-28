package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerSearchOptions extends PageOptions {

  private Long adminId;
  private CompanyType companyType;
  private String query;
}
