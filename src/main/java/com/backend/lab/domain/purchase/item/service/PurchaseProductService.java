package com.backend.lab.domain.purchase.item.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
import com.backend.lab.domain.purchase.item.repository.PurchaseProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseProductService {

  private final PurchaseProductRepository purchaseProductRepository;

  public List<PurchaseProduct> gets() {
    return purchaseProductRepository.findAll();
  }

  public PurchaseProduct getById(Long productId) {
    return purchaseProductRepository.findById(productId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "아이템"));
  }

  @Transactional
  public PurchaseProduct save(PurchaseProduct purchaseProduct) {
    return purchaseProductRepository.save(purchaseProduct);
  }

  @Transactional
  public List<PurchaseProduct> save(List<PurchaseProduct> purchaseProducts) {
    return purchaseProductRepository.saveAll(purchaseProducts);
  }

  public ProductResp productResp(PurchaseProduct product) {
    return ProductResp.builder()
        .id(product.getId())
        .type(product.getType())
        .name(product.getName())
        .price(product.getPrice())
        .amount(product.getAmount())
        .build();
  }
}
