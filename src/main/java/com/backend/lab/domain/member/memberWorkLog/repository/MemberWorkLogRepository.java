package com.backend.lab.domain.member.memberWorkLog.repository;

import com.backend.lab.domain.member.memberWorkLog.entity.MemberWorkLog;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberWorkLogRepository extends JpaRepository<MemberWorkLog, Long>, MemberWorkLogRepositoryCustom{

  @Query("""
  select m from MemberWorkLog m join fetch m.createdBy order by m.createdAt desc
""")
  List<MemberWorkLog> findByOrderByCreatedAtDesc();

  @Query("""
    select m from MemberWorkLog m
    left join fetch m.createdBy c
    left join fetch m.member mem
    left join fetch mem.customerProperties
    order by m.createdAt desc
""")

  List<MemberWorkLog> findRecentLogs(Pageable pageable);

}
