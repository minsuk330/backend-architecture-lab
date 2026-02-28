package com.backend.lab.domain.member.core.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member
    , Long>, MemberRepositoryCustom {

  Optional<Member> findByEmail(String email);

  @Query("select count(m) > 0 from Member m where m.email = :email and m.deletedAt IS NULL")
  boolean existsByEmail(@Param("email")String email);

  Optional<Member> findByProviderAndProviderId(ProviderType providerType, String providerId);


  @Query("select count(m) > 0 from Member m where m.type = :type and m.agentProperties.phoneNumber = :phoneNumber and m.deletedAt IS NULL")
  boolean existsByAgentAndPhoneNumber(@Param("type")MemberType memberType, @Param("phoneNumber")String phoneNumber);
  
  @Query("select count(m) > 0 from Member m where m.type = :type and m.sellerProperties.phoneNumber = :phoneNumber and m.deletedAt IS NULL")
  boolean existsBySellerAndPhoneNumber(@Param("type")MemberType memberType, @Param("phoneNumber")String phoneNumber);
  
  @Query("select count(m) > 0 from Member m where m.type = :type and m.buyerProperties.phoneNumber = :phoneNumber and m.deletedAt IS NULL")
  boolean existsByBuyerAndPhoneNumber(@Param("type")MemberType memberType, @Param("phoneNumber")String phoneNumber);

  @Query("select m from Member m where m.type = :type")
  List<Member> findAllAgent(@Param("type") MemberType type);

  @Query("select m from Member m where m.type = :type and m.deletedAt IS NULL order by m.createdAt desc")
  List<Member> findAllActiveAgents(@Param("type") MemberType type);
}
