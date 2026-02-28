package com.backend.lab.domain.property.sale.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.property.sale.entity.Sale;
import com.backend.lab.domain.property.sale.entity.dto.SaleReq;
import com.backend.lab.domain.property.sale.entity.dto.SaleResp;
import com.backend.lab.domain.property.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;


  public Sale getById(Long id) {
    return saleRepository.findById(id).orElseThrow(
        () -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "sale")
    );
  }
  public Sale getByProperty(Long propertyId) {
    return saleRepository.findByPropertyId(propertyId).orElse(null);
  }

  public void create(SaleReq sale, Long userId, Long propertyId) {
    saleRepository.save(
        Sale.builder()
            .saleAt(sale.getSaleAt())
            .salePrice(sale.getSalePrice())
            .contractorName(sale.getContractorName())
            .contract(sale.getContract())
            .earningPrice(sale.getEarningPrice())
            .memberId(userId)
            .propertyId(propertyId)
            .build()
    );
  }

  public SaleResp saleResp(Sale sale) {
    return SaleResp.builder()
        .propertyId(sale.getPropertyId())
        .earningPrice(sale.getEarningPrice())
        .salePrice(sale.getSalePrice())
        .saleAt(sale.getSaleAt())
        .memberId(sale.getMemberId())
        .build();
  }
}
