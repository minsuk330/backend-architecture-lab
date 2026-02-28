package com.backend.lab.domain.agentItem.core.repository;

import com.backend.lab.domain.agentItem.core.entity.AgentItem;
import com.backend.lab.domain.member.core.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentItemRepository extends JpaRepository<AgentItem, Long> {

  Optional<AgentItem> findByAgent(Member agent);
}
