package com.backend.lab.api.admin.member.seller.facade.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSellerSearchResp {

  private Long id;
  private String email;
  private String name;
  private CompanyType companyType;
  private String phoneNumber;

  private LocalDateTime createdAt;
  private LocalDateTime deletedAt;

  private Boolean isActive;
}
