package com.backend.lab.domain.property.pnuTable.repository;

import com.backend.lab.domain.property.pnuTable.entity.PnuTable;
import com.backend.lab.domain.property.pnuTable.entity.vo.PnuTableType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PnuTableRepository extends JpaRepository<PnuTable, Long> {

  @Query("select  p from  PnuTable  p where p.type = :pnuTableType order by p.sido asc")
  List<PnuTable> findAllByType(@Param("pnuTableType") PnuTableType pnuItemType);

  @Query("select  p from  PnuTable  p where p.type = :pnuTableType and p.sido = :sido order by p.sigungu asc")
  List<PnuTable> findAllByTypeAndSido(
      @Param("pnuTableType") PnuTableType pnuItemType,
      @Param("sido") String sido
  );

  @Query("select  p from  PnuTable  p where p.type = :pnuTableType and p.sido = :sido and p.sigungu = :sigungu order by p.bjd asc")
  List<PnuTable> findAllByTypeAndSidoAndSigungu(
      @Param("pnuTableType") PnuTableType pnuItemType,
      @Param("sido") String sido,
      @Param("sigungu") String sigungu
  );

  @Query("select p from PnuTable p where p.type = :pnuTableType and p.sido = :sidoCode")
  Optional<PnuTable> findBySidoCode(
      @Param("sidoCode") String sidoCode,
      @Param("pnuTableType") PnuTableType pnuTableType
  );

  @Query("select p from PnuTable p where p.type = :pnuTableType and p.sido = :sidoCode and p.sigungu = :sigunguCode")
  Optional<PnuTable> findBySigunguCode(
      @Param("sidoCode") String sidoCode,
      @Param("sigunguCode") String sigunguCode,
      @Param("pnuTableType") PnuTableType pnuTableType
  );

  @Query("select p from PnuTable p where p.type = :pnuTableType and p.sido = :sidoCode and p.sigungu = :sigunguCode and p.bjd = :bjdCode")
  Optional<PnuTable> findByBjdCode(
      @Param("sidoCode") String sidoCode,
      @Param("sigunguCode") String sigunguCode,
      @Param("bjdCode") String bjdCode,
      @Param("pnuTableType") PnuTableType pnuTableType
  );

  Optional<PnuTable> findByPnu(String pnu);

  List<PnuTable> findAllByPnuIn(List<String> pnuList);
}
