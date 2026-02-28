package com.backend.lab.domain.memberNote.repository;

import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.memberNote.entity.MemberNote;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberNoteRepository extends JpaRepository<MemberNote, Long> {

  List<MemberNote> findAllByMember(Member member);

  List<MemberNote> findAllByMemberIdIn(Set<Long> memberIds);
}
