package com.backend.lab.domain.agentItem.core.repository;

import com.backend.lab.domain.agentItem.core.entity.AgentTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentTicketRepository extends JpaRepository<AgentTicket,Long> {

}
