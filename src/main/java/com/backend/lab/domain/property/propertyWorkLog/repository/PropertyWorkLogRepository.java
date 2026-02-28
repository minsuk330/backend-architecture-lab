package com.backend.lab.domain.property.propertyWorkLog.repository;

import com.backend.lab.domain.property.propertyWorkLog.entity.PropertyWorkLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyWorkLogRepository extends JpaRepository<PropertyWorkLog, Long>, PropertyWorkLogRepositoryCustom {

  @Query("select p from PropertyWorkLog p where p.property.id = :propertyId order by p.createdAt desc ")
  List<PropertyWorkLog> findByPropertyOrderByCreatedAtDesc(@Param("propertyId") Long propertyId);


  @Query("""
  select p from PropertyWorkLog p join fetch p.createdBy where p.property.delete_persistent_at is null and p.property.deletedAt is null order by p.createdAt desc
""")
  List<PropertyWorkLog> findByOrderByCreatedAtDesc();

  List<PropertyWorkLog> findAllByCreatedByIdAndCreatedAtBetween(Long adminId, LocalDateTime yesterday, LocalDateTime today);
}
