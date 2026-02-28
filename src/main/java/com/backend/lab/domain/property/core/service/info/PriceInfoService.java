package com.backend.lab.domain.property.core.service.info;

import com.backend.lab.domain.property.core.entity.embedded.LandProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.entity.information.PriceInformation;
import com.backend.lab.domain.property.core.repository.info.PriceInfoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceInfoService {
  private final PriceInfoRepository priceInfoRepository;

  @Transactional
  public PriceInformation create(PriceProperties priceProperties, Long yeonPyongPrice,
      List<LandProperties> lands) {

    setPyengPrice(priceProperties, lands);

    if (priceProperties.getMonthPrice() != null && priceProperties.getDepositPrice() != null
        && priceProperties.getMmPrice() != null) {

      if (priceProperties.getRoi() == null) {
        Long investment = priceProperties.getMmPrice() - priceProperties.getDepositPrice();
        if (investment > 0) {
          BigDecimal monthPrice = BigDecimal.valueOf(priceProperties.getMonthPrice());
          BigDecimal yearlyIncome = monthPrice.multiply(BigDecimal.valueOf(12));
          BigDecimal investmentBd = BigDecimal.valueOf(investment);

          BigDecimal roi = yearlyIncome.divide(investmentBd, 3, RoundingMode.HALF_UP)
              .multiply(BigDecimal.valueOf(100));
          priceProperties.setRoi(roi.setScale(1, RoundingMode.HALF_UP).doubleValue());
        }
      }
    }


    priceProperties.setYeonPyongPrice(yeonPyongPrice);
    return priceInfoRepository.save(
        PriceInformation.builder()
            .properties(priceProperties)
            .build()
    );
  }

  private void setPyengPrice(PriceProperties priceProperties, List<LandProperties> lands) {
    if (lands != null && !lands.isEmpty()) {
      Double totalAreaPyung = lands.stream()
          .filter(land -> land.getAreaPyung() != null)
          .mapToDouble(LandProperties::getAreaPyung)
          .sum();

      if (totalAreaPyung > 0 && priceProperties.getMmPrice() != null) {
        Double pyeongPrice = priceProperties.getMmPrice() / totalAreaPyung;
        priceProperties.setPyeongPrice(pyeongPrice.longValue());
      }
    }
  }

  public PriceInformation getById(Long id) {
    return priceInfoRepository.findById(id).orElse(null);
  }

  @Transactional
  public void update(PriceProperties price, Long priceId, Long yeonPyongPrice,List<LandProperties> lands) {
    price.setYeonPyongPrice(yeonPyongPrice);
    PriceInformation priceInformation = getById(priceId);

    setPyengPrice(price, lands);

    priceInformation.setProperties(price);

  }
}

