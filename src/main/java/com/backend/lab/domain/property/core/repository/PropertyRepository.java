package com.backend.lab.domain.property.core.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<Property, Long>,
    PropertyRepositoryCustom {


  @Query("select p from Property p "
      + "where p.id = :propertyId")
  Optional<Property> findWithFloors(@Param("propertyId") Long propertyId);

  @Query("""
        SELECT DISTINCT p FROM Property p
        LEFT JOIN FETCH p.address addr
        LEFT JOIN FETCH p.price price
        LEFT JOIN FETCH p.register register
        LEFT JOIN FETCH p.template template
        LEFT JOIN FETCH p.bigCategory bigCategory
        LEFT JOIN FETCH p.thumbnailImageUrl thumbnail
        WHERE p.id = :propertyId
        """)
  Optional<Property> findByIdWithAllDetails(@Param("propertyId") Long propertyId);

  @Query("""
        SELECT DISTINCT p FROM Property p
        LEFT JOIN FETCH p.address addr
        LEFT JOIN FETCH p.price price
        LEFT JOIN FETCH p.register register
        LEFT JOIN FETCH p.template template
        LEFT JOIN FETCH p.bigCategory bigCategory
        LEFT JOIN FETCH p.thumbnailImageUrl thumbnail
        WHERE p.id IN :propertyIds
        """)
  List<Property> findByIdInWithAllDetails(@Param("propertyIds") List<Long> propertyIds);

  @Query(value = """
        SELECT DISTINCT p FROM Property p
        LEFT JOIN FETCH p.address addr
        LEFT JOIN FETCH p.price price
        LEFT JOIN FETCH p.bigCategory bigCategory
        ORDER BY p.id
        """,
        countQuery = "SELECT count(p) FROM Property p")
  Page<Property> findAllWithBasicDetails(Pageable pageable);


  long countByAdminId(Long adminId);

  List<Property> findAllByBigCategoryId(Long id);

  @Query("SELECT p FROM Property p JOIN p.smallCategories sc WHERE sc.id = :categoryId")
  List<Property> findAllBySmallCategoryId(@Param("categoryId") Long id);

  @Query("select p from Property p "
      + "join PropertyAdvertisement adv on p = adv.property "
      + "where adv.agent = :agent "
      + "and adv.deletedAt is null "
      + "and adv.endDate >= :now "
      + "and adv.startDate <= :now "
      + "order by p.createdAt desc ")
  Page<Property> getsByAgentAdvAvailable(
      @Param("agent") Member agent,
      @Param("now") LocalDateTime now,
      Pageable pageable
  );

  @Query("select p from Property p "
      + "join PropertyAdvertisement adv on p = adv.property "
      + "where adv.agent = :agent "
      + "and adv.deletedAt is null "
      + "order by p.createdAt desc ")
  Page<Property> getsByAgentAdvAvailable(
      @Param("agent") Member agent,
      Pageable pageable
  );

  @Query("select p from Property p "
      + "where p.exclusiveAgentId = :memberId "
      + "order by p.createdAt desc ")
  Page<Property> getsByExclusiveAgentId(@Param("memberId") Long memberId, Pageable pageable);


  @Query("""
select p from Property p where p.delete_persistent_at is null and p.deletedAt is null order by p.createdAt  desc limit :limit
""")
  List<Property> getsOrderByCreatedAt(@Param("limit") int limit);
         
  @Query("select p from Property p "
      + "where p.address.properties.pnu like concat(:pnu, '%')")
  List<Property> findByPnuStartWith(@Param("pnu") String pnu);

  @Query("""
select p from Property p where p.status=:status and p.isPublic=:isPublic order by p.createdAt desc limit :limit
""")
  List<Property> findProperties(@Param("status") PropertyStatus status, @Param("limit") int limit, @Param("isPublic") Boolean isPublic);

  @Query("""
select p from Property p where p.status=:status order by p.createdAt desc limit :limit
""")
  List<Property> findPropertiesByMember(@Param("status") PropertyStatus status, @Param("limit") int limit);

  @Query("""
select p from Property p order by p.createdAt desc limit :limit
""")
  List<Property> findPropertiesByAgent(@Param("limit") int limit);

  @Modifying
  @Query(value = "insert into property (id, admin_id, building_name, status, is_public, wish_count, big_category_id, created_at, updated_at) values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)", nativeQuery = true)
  void insertPropertyWithId(Long id, Long adminId, String buildingName, String status, Boolean isPublic, Long wishCount, Long bigCategoryId, LocalDateTime createdAt, LocalDateTime updatedAt);

  @Modifying
  @Query(value = "UPDATE property SET created_at = ?2, updated_at = ?3 WHERE id = ?1", nativeQuery = true)
  void updatePropertyDates(Long propertyId, LocalDateTime createdAt, LocalDateTime updatedAt);
  @Query(value = """
        SELECT p.* FROM property p
        WHERE p.id IN :propertyIds
        AND p.deleted_at IS NOT NULL
        AND p.delete_persistent_at IS NULL
        """, nativeQuery = true)
  List<Property> findDeletedByIdInWithAllDetails(@Param("propertyIds") List<Long> propertyIds);

  @Query(value = """
        SELECT p.* FROM property p
        WHERE p.deleted_at IS NOT NULL 
        AND p.delete_persistent_at IS NULL
        ORDER BY p.id
        """,
      countQuery = "SELECT count(*) FROM property WHERE deleted_at IS NOT NULL AND delete_persistent_at IS NULL",
      nativeQuery = true)
  Page<Property> findDeletedAllWithBasicDetails(Pageable pageable);
}





