package com.backend.lab.domain.member.memberWorkLog.service;

import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailOptions;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailResp;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogResp;
import com.backend.lab.api.admin.manageLog.dto.SearchMemberWorkLogOptions;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.memberWorkLog.entity.MemberWorkLog;
import com.backend.lab.domain.member.memberWorkLog.entity.MemberWorkLogDetail;
import com.backend.lab.domain.member.memberWorkLog.entity.vo.MemberWorkLogType;
import com.backend.lab.domain.member.memberWorkLog.repository.MemberWorkLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberWorkLogService {

  private final MemberWorkLogRepository memberWorkLogRepository;


  @Transactional
  public void createLog(List<MemberWorkLogDetail> details, Admin admin,Member member ,String adminIp) {
    MemberWorkLog log = MemberWorkLog.builder()
        .workLogType(MemberWorkLogType.CREATED)
        .details(details)
        .member(member)
        .adminIp(adminIp)
        .createdBy(admin)
        .build();

    memberWorkLogRepository.save(log);
  }


  @Transactional
  public void updateLog(List<MemberWorkLogDetail> details, Admin admin, Member member, String adminIp) {
    MemberWorkLog log = MemberWorkLog.builder()
        .workLogType(MemberWorkLogType.UPDATED)
        .details(details)
        .member(member)
        .adminIp(adminIp)
        .createdBy(admin)
        .build();

    memberWorkLogRepository.save(log);
  }


  public Page<MemberWorkLogResp> gets(SearchMemberWorkLogOptions options) {
    return memberWorkLogRepository.gets(options);
  }

  public Page<MemberWorkLogDetailResp> findDetails(MemberWorkLogDetailOptions options) {
    return memberWorkLogRepository.findDetails(options);
  }

  public List<MemberWorkLog> gets() {
    Pageable pageable = PageRequest.of(0, 100);// 최신 100개
    return memberWorkLogRepository.findRecentLogs(pageable);
  }


  public MemberWorkLogResp memberWorkLogResp(MemberWorkLog log) {

    String adminName =
        log.getCreatedBy().getName() + (log.getCreatedBy().getJobGrade() != null ? " " + log.getCreatedBy().getJobGrade().getName() : "");

    return MemberWorkLogResp.builder()
        .id(log.getId())
        .createdAt(log.getCreatedAt())
        .updatedAt(log.getUpdatedAt())

        .memberId(log.getMember().getId())
        .companyType(log.getMember().getCustomerProperties().getCompanyType())
        .name(log.getMember().getCustomerProperties().getName())
        .phoneNumber(log.getMember().getCustomerProperties().getPhoneNumber())
        .type(log.getWorkLogType())
        .createdName(adminName)
        .ip(log.getAdminIp())
        .build(
        );


  }
}
