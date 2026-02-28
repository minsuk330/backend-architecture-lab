package com.backend.lab.domain.member.core.repository;

import com.backend.lab.domain.member.core.entity.DeletedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long>, DeletedMemberRepositoryCustom{

}
