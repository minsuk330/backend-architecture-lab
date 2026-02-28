package com.backend.lab.domain.member.memberWorkLog.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberLogFieldType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_work_log_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberWorkLogDetail extends BaseEntity {

  @Enumerated(EnumType.STRING)
  private MemberLogFieldType logFieldType;

  private String beforeValue;
  private String afterValue;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_work_log_id")
  private MemberWorkLog memberWorkLog;


}
