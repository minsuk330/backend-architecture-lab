package com.backend.lab.domain.propertyRequest.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRequestRepository extends JpaRepository<PropertyRequest, Long> , PropertyRequestRepositoryCustom {

  List<PropertyRequest> findAllByRequester(Member member);

  @Query("""
select p from PropertyRequest p where p.requester = :member order by p.createdAt desc 
""")
  Page<PropertyRequest> findAllByRequester(
      @Param("member") Member member,
      Pageable pageable);
}
