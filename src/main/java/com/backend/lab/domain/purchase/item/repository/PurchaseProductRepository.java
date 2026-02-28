package com.backend.lab.domain.purchase.item.repository;

import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, Long> {

}
