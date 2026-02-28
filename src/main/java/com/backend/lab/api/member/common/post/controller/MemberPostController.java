package com.backend.lab.api.member.common.post.controller;

import static com.backend.lab.common.util.AuthUtil.getUserIdWithAnonymous;

import com.backend.lab.api.member.common.post.facade.MemberPostFacade;
import com.backend.lab.common.auth.annotation.RequireMemberRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.dto.resp.PostResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireMemberRole
@RestController
@RequestMapping("/member/post")
@RequiredArgsConstructor
@Tag(name="[회원/공용] 게시판")
public class MemberPostController {

  private final MemberPostFacade agentPostFacade;

  @Operation(summary = "게시글 검색")
  @GetMapping
  public PageResp<PostResp> search(
      @ModelAttribute SearchPostOptions options
  ) {
    return agentPostFacade.search(options, getUserIdWithAnonymous());
  }

  @Operation(summary = "게시글 상세 조회")
  @GetMapping("/{postId}")
  public PostResp getPostById(
      @PathVariable("postId") Long postId
  ) {
    return agentPostFacade.getById(postId);
  }

}
