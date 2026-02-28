package com.backend.lab.domain.memberNote.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.memberNote.entity.vo.MemberNoteType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberNote extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private Long adminId;

  public void setMember(Member member) {
    this.member = member;
    if (member != null) {
      member.getMemberNotes().add(this);
    }
  }

  public void detachMember() {
    if (member != null) {
      member.getMemberNotes().remove(this);
    }
    this.member = null;
  }

  @Column(length = 3000)
  private String content;

  @Enumerated
  private MemberNoteType type;
}
