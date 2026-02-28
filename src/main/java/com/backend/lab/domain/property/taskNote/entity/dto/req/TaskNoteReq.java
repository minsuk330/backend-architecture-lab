package com.backend.lab.domain.property.taskNote.entity.dto.req;

import com.backend.lab.api.admin.property.core.dto.req.PropertyTaskNoteUpdateReq;
import java.util.List;
import lombok.Getter;

@Getter
public class TaskNoteReq {
  List<PropertyTaskNoteUpdateReq> taskNoteReqs;
}
