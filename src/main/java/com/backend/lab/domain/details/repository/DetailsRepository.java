package com.backend.lab.domain.details.repository;

import com.backend.lab.domain.details.entity.Details;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetailsRepository extends JpaRepository<Details, Long> {

  @Query("select d from Details d where d.property.id = :propertyId")
  Details findByProperty(@Param("propertyId") Long propertyId);

  List<Details> findAllByPropertyIdIn(List<Long> propertyIds);
}
