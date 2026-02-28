package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.repository.info.LedgeInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgeInfoService {

  private final LedgeInfoRepository ledgeInfoRepository;

  @Transactional
  public LedgeInformation create(LedgeInformation ledgerInformationq) {
    return ledgeInfoRepository.save(ledgerInformationq);
  }

  public LedgeInformation getById(Long id) {
    return ledgeInfoRepository.findById(id).orElse(null);
  }



  @Transactional
  public void update(LedgerProperties ledge, Long ledgeId) {
    LedgeInformation ledgeInformation = getById(ledgeId);

    ledgeInformation.setProperties(ledge);
  }


  @Transactional
  public void delete(Long id) {
    ledgeInfoRepository.deleteById(id);
  }


}
