package com.backend.lab.domain.memberNote.entity.dto.req;

import com.backend.lab.domain.memberNote.entity.vo.MemberNoteType;
import lombok.Getter;

@Getter
public class MemberNoteDTO {

  private MemberNoteType type;
  private String content;
}
