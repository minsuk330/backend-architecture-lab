package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.api.admin.property.core.dto.req.PropertyAddressReq;
import com.backend.lab.common.util.PnuComponents;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.information.AddressInformation;
import com.backend.lab.domain.property.core.repository.info.AddressInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressInfoService {

  private final AddressInfoRepository addressInfoRepository;

  @Transactional
  public AddressInformation create(AddressProperties addressProperties) {
    return addressInfoRepository.save(
        AddressInformation.builder()
            .properties(addressProperties)
            .build()
    );

  }

  @Transactional
  public AddressInformation create(PropertyAddressReq req) {
    Integer bun = null;
    Integer ji = null;
    if (req.getPnu() != null) {
      PnuComponents pnuComponents = PnuComponents.parse(req.getPnu());
       bun= Integer.valueOf(pnuComponents.getBun());
       ji = Integer.valueOf(pnuComponents.getJi());
    }

    return addressInfoRepository.save(
        AddressInformation.builder()
            .properties(
                AddressProperties.builder()
                    .pnu(req.getPnu())
                    .roadAddress(req.getRoadAddress())
                    .jibunAddress(req.getJibunAddress())
                    .lat(req.getLat())
                    .lng(req.getLng())
                    .bunCode(bun)
                    .jiCode(ji)
                    .build()
            )
            .build()
    );

  }

  public AddressInformation getById(Long id) {
    return addressInfoRepository.findById(id).orElse(null);
  }

  public AddressInformation getByPnu(String pnu) {
    return addressInfoRepository.findByPnu(pnu);
  }

  @Transactional
  public void update(AddressProperties address, Long addressId) {
    AddressInformation addressInformation = getById(addressId);

    addressInformation.setProperties(address);

  }
}
