package com.backend.lab.domain.secret.repository;

import com.backend.lab.domain.secret.entity.Secret;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretRepository extends JpaRepository<Secret, Long> {


  @Query("select s from Secret s where s.property.id = :propertyId and s.createdBy.id = :adminId")
  Optional<Secret> findByPropertyAndCreatedBy(@Param("propertyId") Long propertyId,
      @Param("adminId") Long adminId);

  @Query("select s from Secret s " +
         "left join fetch s.createdBy cb " +
         "left join fetch cb.department " +
         "left join fetch cb.permission " +
         "where s.property.id = :propertyId")
  List<Secret> findAllByPropertyId(@Param("propertyId") Long propertyId);


  @Query("select s from Secret s " +
      "left join fetch s.createdBy cb " +
      "left join fetch cb.department " +
      "left join fetch cb.permission " +
      "where s.property.id in :propertyIds")
  List<Secret> findAllByPropertyIdIn(@Param("propertyIds") List<Long> propertyIds);
}
