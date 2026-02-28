package com.backend.lab.domain.purchase.core.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.purchase.core.entity.Purchase;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.dto.PurchaseResp;
import com.backend.lab.domain.purchase.core.entity.dto.SearchPurchaseOptions;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import com.backend.lab.domain.purchase.core.repository.PurchaseItemRepository;
import com.backend.lab.domain.purchase.core.repository.PurchaseRepository;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final PurchaseItemRepository purchaseItemRepository;

  public Purchase getByOrderNumber(String orderNumber) {
    return purchaseRepository.findByOrderNumber(orderNumber)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "주문"));
  }

  public Page<PurchaseItem> getItemsByAgent(Member agent, Pageable pageable) {
    return purchaseItemRepository.findAllByMember(agent, PurchaseStatus.SUCCESS, pageable);
  }

  public List<Purchase> getsByAgent(Member agent) {
    return purchaseRepository.findAllByMember(agent);
  }

  public Page<PurchaseItem> search(SearchPurchaseOptions options) {
    return purchaseItemRepository.search(options);
  }

  @Transactional
  public Purchase create(Long memberId, PurchaseMethod method, String orderNumber,
      Map<PurchaseProduct, Integer> countPerProduct) {

    if (countPerProduct.isEmpty()) {
      throw new IllegalArgumentException("Product cannot be empty");
    }

    String name = "";
    Set<PurchaseProduct> products = countPerProduct.keySet();

    Set<PurchaseItem> items = new HashSet<>();
    int totalPrice = 0;
    for (PurchaseProduct product : products) {
      if (name.isEmpty()) {
        name = product.getName();
      }

      Integer amount = countPerProduct.get(product);
      PurchaseItem item = PurchaseItem.builder()
          .product(product)
          .amount(amount)
          .totalPrice(product.getPrice() * amount)
          .build();
      totalPrice += item.getTotalPrice();
      items.add(item);
    }

    if (products.size() > 1) {
      name += " 외 " + (products.size() - 1) + "개";
    }

    Purchase purchase = Purchase.builder()
        .memberId(memberId)
        .name(name)
        .status(PurchaseStatus.NOT_PAID)
        .method(method)
        .orderNumber(orderNumber)
        .totalPrice(totalPrice)
        .items(new HashSet<>())
        .build();

    items.forEach(item -> item.setPurchase(purchase));
    return purchaseRepository.save(purchase);
  }

  public boolean hasPurchasedAdvertise(Member agent) {
    return purchaseRepository.hasPurchasedAdvertise(agent, ProductType.ADVERTISE);
  }

  public PurchaseResp purchaseResp(PurchaseItem item, String memberName, Long memberId) {
    Purchase purchase = item.getPurchase();
    return PurchaseResp.builder()
        .purchasedAt(purchase.getCreatedAt())
        .orderNumber(purchase.getOrderNumber())
        .name(item.getProduct().getName())
        .totalPrice(item.getTotalPrice())
        .amount(item.getAmount())
        .id(memberId)
        .buyerName(memberName)
        .method(purchase.getMethod())
        .status(purchase.getStatus())
        .build();
  }
}
