package com.backend.lab.domain.memberNote.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.memberNote.entity.MemberNote;
import com.backend.lab.domain.memberNote.entity.dto.req.MemberNoteDTO;
import com.backend.lab.domain.memberNote.entity.dto.resp.MemberNoteResp;
import com.backend.lab.domain.memberNote.repository.MemberNoteRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberNoteService {

  private final MemberNoteRepository memberNoteRepository;

  public MemberNote getById(Long id) {
    return memberNoteRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "회원 메모"));
  }

  public List<MemberNote> getsByMember(Member member) {
    return memberNoteRepository.findAllByMember(member);
  }

  public List<MemberNote> getsByMemberIn(Set<Long> memberIds) {
    return memberNoteRepository.findAllByMemberIdIn(memberIds);
  }

  @Transactional
  public MemberNote create(Member member, Long adminId, MemberNoteDTO dto) {
    MemberNote memberNote = MemberNote.builder()
        .type(dto.getType())
        .content(dto.getContent())
        .adminId(adminId)
        .build();

    memberNote.setMember(member);
    return memberNoteRepository.save(
        memberNote
    );
  }

  @Transactional
  public MemberNote update(Long id, MemberNoteDTO dto) {
    MemberNote memberNote = this.getById(id);
    memberNote.setType(dto.getType());
    memberNote.setContent(dto.getContent());
    return memberNoteRepository.save(memberNote);
  }

  @Transactional
  public void delete(Long id) {
    MemberNote memberNote = this.getById(id);
    memberNote.detachMember();
    memberNoteRepository.delete(memberNote);
  }

  public MemberNoteResp memberNoteResp(MemberNote note, AdminResp admin) {
    return MemberNoteResp.builder()
        .id(note.getId())
        .type(note.getType())
        .createdAt(note.getCreatedAt())
        .content(note.getContent())
        .admin(admin)
        .build();
  }
}
