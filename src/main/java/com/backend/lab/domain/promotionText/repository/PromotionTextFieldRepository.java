package com.backend.lab.domain.promotionText.repository;

import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.PromotionTextField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionTextFieldRepository extends JpaRepository<PromotionTextField, Long> {


  @Modifying
  @Query("DELETE FROM PromotionTextField p WHERE p.promotionText = :promotionText")
  void deleteByPromotionText(@Param("promotionText") PromotionText promotionText);

}
