package com.backend.lab.api.admin.manageLog.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberWorkLogType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Getter
public class MemberWorkLogResp extends BaseResp {
  private MemberWorkLogType type;
  private Long memberId;
  private CompanyType companyType;
  private String name;
  private String phoneNumber;
  private String createdName;
  private String ip;

  // 프로젝션용 생성자
  public MemberWorkLogResp(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,MemberWorkLogType type,
                          CompanyType companyType,
                          Long memberId, String name, String phoneNumber, 
                          String createdName, String ip) {
    super(id, createdAt, updatedAt, null);
    this.type = type;
    this.companyType = companyType;
    this.memberId = memberId;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.createdName = createdName;
    this.ip = ip;
  }
}
