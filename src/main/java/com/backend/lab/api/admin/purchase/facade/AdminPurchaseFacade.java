package com.backend.lab.api.admin.purchase.facade;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.dto.PurchaseResp;
import com.backend.lab.domain.purchase.core.entity.dto.SearchPurchaseOptions;
import com.backend.lab.domain.purchase.core.service.PurchaseService;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
import com.backend.lab.domain.purchase.item.service.PurchaseProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPurchaseFacade {

  private final PurchaseService purchaseService;
  private final PurchaseProductService productService;
  private final AgentService agentService;

  public PageResp<PurchaseResp> gets(SearchPurchaseOptions options) {
    Page<PurchaseItem> page = purchaseService.search(options);
    List<PurchaseResp> data = page.getContent().stream()
        .map(p -> {
          Long memberId = p.getPurchase().getMemberId();
          Member agent = agentService.getById(memberId);
          return purchaseService.purchaseResp(p, agent.getAgentProperties().getName(),memberId);
        })
        .toList();
    return new PageResp<>(page, data);
  }

  public ListResp<ProductResp> getProducts() {
    List<PurchaseProduct> rows = productService.gets();
    List<ProductResp> products = rows.stream()
        .map(productService::productResp)
        .toList();
    return new ListResp<>(products);
  }
}
