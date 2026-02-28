package com.backend.lab.api.member.agent.propertyModal.facade.dto.resp;

import com.backend.lab.api.admin.property.customer.dto.CustomerPropertyResp;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPropertyContactResp {

  private Boolean canView;
  private List<CustomerPropertyResp> customers;

  private List<TaskNoteResp>taskNotes;
}
