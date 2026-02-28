package com.backend.lab.api.admin.member.memberNote.facade;

import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.memberNote.entity.dto.req.MemberNoteDTO;
import com.backend.lab.domain.memberNote.service.MemberNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberNoteFacade {

  private final MemberService memberService;
  private final MemberNoteService memberNoteService;

  @Transactional
  public void create(Long memberId, Long adminId, MemberNoteDTO dto) {
    memberNoteService.create(
        memberService.getById(memberId),
        adminId,
        dto
    );
  }

  @Transactional
  public void update(Long noteId, MemberNoteDTO dto) {
    memberNoteService.update(noteId, dto);
  }

  @Transactional
  public void delete(Long noteId) {
    memberNoteService.delete(noteId);
  }
}
