package com.backend.lab.domain.member.memberWorkLog.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberWorkLogType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_work_log")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberWorkLog extends BaseEntity {

  @Enumerated(EnumType.STRING)
  private MemberWorkLogType workLogType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private Admin createdBy;

  private String adminIp;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "member_work_log_id")
  @Builder.Default
  private List<MemberWorkLogDetail> details = new ArrayList<>();


}
