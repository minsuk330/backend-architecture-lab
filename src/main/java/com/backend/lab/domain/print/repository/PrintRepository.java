package com.backend.lab.domain.print.repository;

import com.backend.lab.domain.print.entity.Print;
import com.backend.lab.domain.print.entity.vo.PrintType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintRepository extends JpaRepository<Print, Long> {


  Optional<Print> findByPrintType(PrintType printType);

  Optional<Print> findByPrintTypeAndAgentId(PrintType printType, Long agentId);

  Optional<Print> findByPrintTypeAndAgentIdIsNull(PrintType printType);
}
