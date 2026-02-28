package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.repository.info.LandInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LandInfoService {

  private final LandInfoRepository landInfoRepository;

  @Transactional
  public LandInformation create(LandInformation landInformation) {
    return landInfoRepository.save(landInformation);
  }

  public LandInformation getById(Long id) {
    return landInfoRepository.findById(id).orElse(null);
  }

  @Transactional
  public void update(LandProperties land, Long landId) {
    LandInformation landInformation = getById(landId);

    landInformation.setProperties(land);
  }

  @Transactional
  public void delete(Long id) {
    landInfoRepository.deleteById(id);
  }
}
