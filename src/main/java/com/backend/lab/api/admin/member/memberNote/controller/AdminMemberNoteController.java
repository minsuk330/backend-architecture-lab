package com.backend.lab.api.admin.member.memberNote.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.member.memberNote.facade.AdminMemberNoteFacade;
import com.backend.lab.domain.memberNote.entity.dto.req.MemberNoteDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/member-note")
@Tag(name = "[관리자/회원관리] 고객 메모")
public class AdminMemberNoteController {

  private final AdminMemberNoteFacade adminMemberNoteFacade;

  @Operation(summary = "생성")
  @PostMapping("/{memberId}")
  public void crate(
      @PathVariable("memberId") Long memberId,
      @RequestBody MemberNoteDTO memberNoteDTO
  ) {
    adminMemberNoteFacade.create(memberId, getUserId(), memberNoteDTO);
  }

  @Operation(summary = "수정")
  @PutMapping("/{noteId}")
  public void update(
      @PathVariable("noteId") Long noteId,
      @RequestBody MemberNoteDTO memberNoteDTO
  ) {
    adminMemberNoteFacade.update(noteId, memberNoteDTO);
  }

  @Operation(summary = "삭제")
  @DeleteMapping("/{noteId}")
  public void delete(
      @PathVariable("noteId") Long noteId
  ) {
    adminMemberNoteFacade.delete(noteId);
  }
}
