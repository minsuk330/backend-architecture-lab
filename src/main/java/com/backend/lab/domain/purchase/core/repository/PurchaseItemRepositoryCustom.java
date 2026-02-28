package com.backend.lab.domain.purchase.core.repository;

import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.dto.SearchPurchaseOptions;
import org.springframework.data.domain.Page;

public interface PurchaseItemRepositoryCustom {

  Page<PurchaseItem> search(SearchPurchaseOptions options);
}
