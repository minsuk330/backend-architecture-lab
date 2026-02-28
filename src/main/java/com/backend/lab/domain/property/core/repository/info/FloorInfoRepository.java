package com.backend.lab.domain.property.core.repository.info;

import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import java.util.LinkedHashSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FloorInfoRepository extends JpaRepository<FloorInformation,Long> {

  @Query("select f from FloorInformation f where f.buildingOrder = :buildingOrder order by f.rank asc " )
  LinkedHashSet<FloorInformation> findByBuildingOrder(@Param("buildingOrder") Long buildingOrder);
}
