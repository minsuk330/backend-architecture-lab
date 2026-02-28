package com.backend.lab.domain.member.audit.accessLog.repository;

import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.req.GetMemberAccessLogOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberAccessLogRepositoryCustom {

  Page<MemberAccessLog> findLatestLogsEachUser(Pageable pageable);

  Page<MemberAccessLog> search(GetMemberAccessLogOptions options);
}
