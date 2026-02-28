package com.backend.lab.api.member.common.post.facade;

import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.post.entity.Post;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.dto.resp.PostResp;
import com.backend.lab.domain.post.entity.vo.PostViewType;
import com.backend.lab.domain.post.service.PostService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberPostFacade {

  private final PostService postService;
  private final MemberService memberService;

  @Transactional(readOnly = true)
  public PostResp getById(Long postId) {
    Post post = postService.getById(postId);
    return this.postResp(post);
  }

  @Transactional(readOnly = true)
  public PageResp<PostResp> search(SearchPostOptions options, Long userId) {
    Member member = memberService.getById(userId);
    PostViewType viewType;
    viewType = switch (member.getType()) {
      case AGENT -> PostViewType.AGENT;
      case BUYER -> PostViewType.BUYER;
      case SELLER -> PostViewType.SELLER;
      default -> throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    };

    Page<Post> page = postService.search(options, viewType);

    List<PostResp> data = page.getContent().stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(Post::getCreatedAt).reversed()))
        .map(this::postResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public PostResp postResp(Post post) {
    return postService.postResp(post, null);
  }
}
