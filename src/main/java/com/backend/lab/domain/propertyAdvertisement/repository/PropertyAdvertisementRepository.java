package com.backend.lab.domain.propertyAdvertisement.repository;

import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyAdvertisementRepository extends JpaRepository<PropertyAdvertisement, Long> {

  // 특정 매물의 광고 목록 조회
  //광고 시작일. ㅗㅇ료일 추가해야 함
  @Query("SELECT pa FROM PropertyAdvertisement pa " +
          "join fetch pa.agent "+
         "WHERE pa.property.id = :propertyId "
      + "AND pa.startDate <= :now "
      + "AND pa.endDate >= :now " +
         "ORDER BY pa.createdAt DESC")
  List<PropertyAdvertisement> findByPropertyId(
      @Param("propertyId") Long propertyId, @Param("now") LocalDateTime now);

  // 특정 중개사의 광고 목록 조회
  @Query("SELECT pa FROM PropertyAdvertisement pa " +
         "WHERE pa.agent.id = :agentId " +
         "ORDER BY pa.createdAt DESC")
  List<PropertyAdvertisement> findByAgentId(@Param("agentId") Long agentId);

  @Query("SELECT pa FROM PropertyAdvertisement pa " +
      "WHERE pa.property.id = :propertyId "
      + "AND pa.agent.id = :userId "
      + "AND pa.startDate <= :now "
      + "AND pa.endDate >= :now " +
      "ORDER BY pa.createdAt DESC")
  Optional<PropertyAdvertisement> findByPropertyIdAndAgentId(
      @Param("propertyId") Long propertyId,
      @Param("userId") Long userId,
      @Param("now") LocalDateTime now
  );
}
