package com.backend.lab.domain.propertyMember.repositroy;

import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyMemberRepository extends JpaRepository<PropertyMember, Long> {

  @Query("select p from PropertyMember p where p.propertyId = :propertyId")
  List<PropertyMember> findAllByProperty(@Param("propertyId") Long propertyId);

  @Query("select p from PropertyMember p where p.memberId = :memberId")
  List<PropertyMember> findAllByMemberId(@Param("memberId") Long memberId);



  boolean existsByMemberIdAndPropertyId(Long memberId, Long propertyId);

  @Query("select p from PropertyMember p where p.memberId = :memberId and p.propertyId = :propertyId")
  Optional<PropertyMember> findByMemberAndProperty(@Param("memberId") Long memberId, @Param("propertyId") Long propertyId);

  List<PropertyMember> findAllByPropertyIdIn(List<Long> propertyIds);
}
