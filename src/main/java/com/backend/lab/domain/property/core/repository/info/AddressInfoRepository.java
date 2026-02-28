package com.backend.lab.domain.property.core.repository.info;


import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressInfoRepository extends JpaRepository<AddressInformation, Long> {

  @Query("select a from AddressInformation a where a.properties.pnu = :pnu")
  AddressInformation findByPnu(@Param("pnu") String pnu);
}
