package com.backend.lab.domain.member.audit.accessLog.repository;

import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.vo.MemberAccessLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberAccessLogRepository extends JpaRepository<MemberAccessLog, Long>, MemberAccessLogRepositoryCustom {

  MemberAccessLog findTopByMemberIdAndTypeOrderByCreatedAtDesc(Long memberId, MemberAccessLogType type);
  @Query("SELECT m FROM MemberAccessLog m WHERE m.memberId = :memberId ORDER BY m.createdAt DESC")
  Page<MemberAccessLog> findAllByMemberId(Long memberId, Pageable pageable);
}
