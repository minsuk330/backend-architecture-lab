package com.backend.lab.domain.purchase.core.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long>,
    PurchaseItemRepositoryCustom {

  @Query("SELECT p "
      + "FROM PurchaseItem p "
      + "WHERE p.purchase.memberId = :#{#member.id} "
      + "AND p.purchase.status = :status "
      + "ORDER BY p.createdAt DESC")
  Page<PurchaseItem> findAllByMember(@Param("member") Member member, @Param("status") PurchaseStatus status, Pageable pageable);

  @Query("SELECT p "
      + "FROM PurchaseItem p "
      + "WHERE p.purchase.memberId = :#{#member.id} "
      + "ORDER BY p.createdAt DESC")
  List<PurchaseItem> findAllByMember(@Param("member") Member agent);
}
