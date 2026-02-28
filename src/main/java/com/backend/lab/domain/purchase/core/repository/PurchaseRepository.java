package com.backend.lab.domain.purchase.core.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.purchase.core.entity.Purchase;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

  Optional<Purchase> findByOrderNumber(String orderNumber);

  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
      "FROM Purchase p " +
      "JOIN p.items pi " +
      "JOIN pi.product pp " +
      "WHERE p.memberId = :#{#member.id} " +
      "AND p.status = 'SUCCESS' " +
      "AND pp.type = :productType")
  boolean hasPurchasedAdvertise(
      @Param("member") Member member,
      @Param("productType") ProductType productType
  );

  @Query("SELECT p "
      + "FROM Purchase p "
      + "WHERE p.memberId = :#{#member.id} "
      + "ORDER BY p.createdAt DESC")
  List<Purchase> findAllByMember(@Param("member") Member agent);
}
