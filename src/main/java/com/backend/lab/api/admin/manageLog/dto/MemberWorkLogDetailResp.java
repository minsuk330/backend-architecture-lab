package com.backend.lab.api.admin.manageLog.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberLogFieldType;
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

public class MemberWorkLogDetailResp extends BaseResp {
  private MemberLogFieldType fieldType;
  private MemberWorkLogType workLogType;
  private String beforeValue;
  private String afterValue;
  private String adminName;

  // 프로젝션용 생성자
  public MemberWorkLogDetailResp(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                                MemberLogFieldType fieldType, MemberWorkLogType workLogType,
                                String beforeValue, String afterValue, String adminName) {
    super(id, createdAt, updatedAt, null); // BaseResp 생성자 호출
    this.fieldType = fieldType;
    this.workLogType = workLogType;
    this.beforeValue = beforeValue;
    this.afterValue = afterValue;
    this.adminName = adminName;
  }
}
