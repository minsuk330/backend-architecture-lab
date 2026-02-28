package com.backend.lab.domain.wishlist.repository;

import com.backend.lab.domain.wishlist.entity.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

  @Query("select w from Wishlist w where w.member.id = :memberId and w.property.id = :propertyId")
  Optional<Wishlist> findByMemberAndProperty(@Param("memberId") Long memberId,@Param("propertyId") Long propertyId);

  @Query("select w from Wishlist w where w.member.id = :memberId")
  Optional<Wishlist> getByMemberId(Long memberId);

  @Query("select w from Wishlist w where w.member.id = :memberId and w.property.id in :propertyIds")
  List<Wishlist> findByMemberIdAndPropertyIds(@Param("memberId") Long memberId, @Param("propertyIds") List<Long> propertyIds);
}
