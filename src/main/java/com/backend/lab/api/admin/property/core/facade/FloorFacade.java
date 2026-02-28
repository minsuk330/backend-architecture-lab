package com.backend.lab.api.admin.property.core.facade;

import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.FloorProperties;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.core.service.info.FloorInfoService;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FloorFacade {
  private final FloorInfoService floorInfoService;
  private final PropertyService propertyService;

  public Set<FloorInformation> floorsAssignRank(Set<FloorProperties> floors, Boolean isPublic, Long buildingOrder) {

    AtomicLong rankCounter = new AtomicLong(1);

    return floors.stream()
        .sorted((f1, f2) -> getFloorPriority(f1.getFloor()) - getFloorPriority(f2.getFloor()))
        .map(floor -> floorInfoService.create(
            FloorInformation.builder()
                .properties(floor)
                .rank(rankCounter.getAndIncrement())
                .buildingOrder(buildingOrder)
                .isPublic(isPublic != null ? isPublic : true)
                .build()))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  @Transactional
  public void deleteAllFloor(Long propertyId, Long floorId) {

    Property property = propertyService.getById(propertyId);

    // 2. 기존 FloorInformation들의 ID 수집
    Set<Long> floorIds = new LinkedHashSet<>();
    if (property.getFloors() != null && !property.getFloors().isEmpty()) {
      floorIds = property.getFloors().stream()
          .map(FloorInformation::getId)
          .collect(Collectors.toSet());
    }

    // 3. Property에서 연관관계 제거
    if (property.getFloors() != null) {
      property.getFloors().clear();
    }

    // 4. FloorInformation 엔티티들 삭제
    floorIds.forEach(floorInfoService::deleteSingleFloor);

    property.getFloors().clear();
  }

  private int getFloorPriority(String floorName) {
    if (floorName.startsWith("옥탑")) {
      // 옥탑: 높은 층이 먼저 (옥탑3층=1, 옥탑2층=2, 옥탑1층=3)
      int num = extractNumber(floorName.substring(2));
      return 100 - num;  // 큰 수일수록 작은 우선순위
    } else if (floorName.startsWith("지")) {
      // 지하: 낮은 층이 먼저 (지1층=1001, 지2층=1002, 지3층=1003)
      int num = extractNumber(floorName.substring(1));
      return 1000 + num;
    } else {
      // 지상: 높은 층이 먼저 (10층=90, 5층=95, 1층=99)
      int num = extractNumber(floorName);
      return 200 - num;  // 큰 수일수록 작은 우선순위
    }
  }

  private int extractNumber(String str) {
    try {
      return Integer.parseInt(str.replaceAll("[^0-9]", ""));
    } catch (Exception e) {
      return 0;
    }
  }
}
