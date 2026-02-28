package com.backend.lab.domain.member.audit.accessLog.service;

import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.req.GetMemberAccessLogOptions;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.resp.MemberAccessLogResp;
import com.backend.lab.domain.member.audit.accessLog.entity.vo.MemberAccessLogType;
import com.backend.lab.domain.member.audit.accessLog.repository.MemberAccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAccessLogService {

  private final MemberAccessLogRepository memberAccessLogRepository;

  @Transactional
  public void createLoginLog(Long memberId, String ipAddress) {
    memberAccessLogRepository.save(
        MemberAccessLog.builder()
            .memberId(memberId)
            .ipAddress(ipAddress)
            .type(MemberAccessLogType.LOGIN)
            .build()
    );
  }

  @Transactional
  public void createLogoutLog(Long memberId, String ipAddress) {
    memberAccessLogRepository.save(
        MemberAccessLog.builder()
            .memberId(memberId)
            .ipAddress(ipAddress)
            .type(MemberAccessLogType.LOGOUT)
            .build()
    );
  }

  public Page<MemberAccessLog> search(GetMemberAccessLogOptions options) {
    return memberAccessLogRepository.search(options);
  }

  public MemberAccessLog getLatestByMemberId(Long memberId) {
    return memberAccessLogRepository.findTopByMemberIdAndTypeOrderByCreatedAtDesc(memberId, MemberAccessLogType.LOGIN);
  }

  public Page<MemberAccessLog> getsByMemberId(Long memberId, Pageable pageable) {
    return memberAccessLogRepository.findAllByMemberId(memberId, pageable);
  }

  public Page<MemberAccessLog> getsLatestLogsEachUser(Pageable pageable) {
    return memberAccessLogRepository.findLatestLogsEachUser(pageable);
  }

  public MemberAccessLogResp memberAccessLogResp(MemberAccessLog memberAccessLog) {

    if (memberAccessLog == null) {
      return null;
    }

    return MemberAccessLogResp.builder()
        .id(memberAccessLog.getId())
        .createdAt(memberAccessLog.getCreatedAt())
        .ipAddress(memberAccessLog.getIpAddress())
        .memberId(memberAccessLog.getMemberId())
        .type(memberAccessLog.getType())
        .build();
  }
}
