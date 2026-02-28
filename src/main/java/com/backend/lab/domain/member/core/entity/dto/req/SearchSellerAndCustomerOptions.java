package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchSellerAndCustomerOptions extends PageOptions {
  private String query;
  private MemberType memberType;
  private String adminName;
  private Long adminId;
}
