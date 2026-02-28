package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.repository.info.FloorInfoRepository;
import java.util.LinkedHashSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FloorInfoService {

  private final FloorInfoRepository floorInfoRepository;


  @Transactional
  public FloorInformation create(FloorInformation floorInformation) {
    return floorInfoRepository.save(
        FloorInformation.builder()
            .properties(floorInformation.getProperties())
            .isPublic(floorInformation.getIsPublic())
            .buildingOrder(floorInformation.getBuildingOrder())
            .rank(floorInformation.getRank())
            .build()
    );
  }

  public FloorInformation getById(Long id) {
    return floorInfoRepository.findById(id).orElse(null);
  }

  public LinkedHashSet<FloorInformation> getByBuildingOrder(Long buildingOrder) {
    return floorInfoRepository.findByBuildingOrder(buildingOrder);
  }

  @Transactional
  public void deleteSingleFloor(Long floorId) {
    floorInfoRepository.deleteById(floorId);
  }
}
