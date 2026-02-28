package com.backend.lab.domain.property.sale.repository;

import com.backend.lab.domain.property.sale.entity.Sale;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaleRepository extends JpaRepository<Sale, Long> {


  @Query("select s from Sale s where s.propertyId = :propertyId")
  Optional<Sale> findByPropertyId(@Param("propertyId") Long propertyId);
}
