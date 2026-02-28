package com.backend.lab.domain.promotionText.repository;

import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.vo.PromotionMemberType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionTextRepository extends JpaRepository<PromotionText, Long> {

  @Query("select p from PromotionText p where p.memberType = :type")
  Optional<PromotionText> findByMemberType(@Param("type") PromotionMemberType type);

  @Query("select p from PromotionText p where p.memberType = :type and p.agentId = :agentId")
  Optional<PromotionText> findByMemberTypeAndAgentId(@Param("type") PromotionMemberType type,@Param("agentId") Long agentId);
}
