package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import com.backend.lab.domain.property.core.entity.information.RegisterInformation;
import com.backend.lab.domain.property.core.repository.info.RegisterInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterInfoService {
  private final RegisterInfoRepository registerInfoRepository;

  @Transactional
  public RegisterInformation create(RegisterProperties registerProperties) {
    return registerInfoRepository.save(
        RegisterInformation.builder()
            .properties(registerProperties)
            .build()
    );
  }

  public RegisterInformation getById(Long id) {
    return registerInfoRepository.findById(id).orElse(null);
  }

  @Transactional
  public void update(RegisterProperties register, Long registerId) {
    RegisterInformation registerInformation = getById(registerId);
    registerInformation.setProperties(register);
  }
}
