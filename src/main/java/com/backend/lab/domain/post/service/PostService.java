package com.backend.lab.domain.post.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.post.entity.Post;
import com.backend.lab.domain.post.entity.dto.req.PostCreateReq;
import com.backend.lab.domain.post.entity.dto.req.PostUpdateReq;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.dto.resp.PostResp;
import com.backend.lab.domain.post.entity.vo.PostViewType;
import com.backend.lab.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;

  public Post getById(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Post"));
  }

  public Page<Post> search(SearchPostOptions options, PostViewType viewType) {
    return postRepository.search(options, viewType);
  }

  @Transactional
  public void create(Admin admin, PostCreateReq req) {
    postRepository.save(
        Post.builder()

            .createdBy(admin)

            .canAgentView(req.getCanAgentView())
            .canBuyerView(req.getCanBuyerView())
            .canSellerView(req.getCanSellerView())

            .type(req.getType())
            .title(req.getTitle())
            .content(req.getContent())
            .build()
    );
  }

  @Transactional
  public void update(Long postId, PostUpdateReq req) {
    Post post = this.getById(postId);

    post.setTitle(req.getTitle());
    post.setContent(req.getContent());

    post.setCanAgentView(req.getCanAgentView());
    post.setCanBuyerView(req.getCanBuyerView());
    post.setCanSellerView(req.getCanSellerView());
  }

  @Transactional
  public void delete(Long postId) {
    Post post = getById(postId);
    postRepository.delete(post);
  }

  public PostResp postResp(Post post, AdminResp createBy) {
    return PostResp.builder()
        .id(post.getId())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())

        .createdBy(createBy)

        .canAgentView(post.getCanAgentView())
        .canBuyerView(post.getCanBuyerView())
        .canSellerView(post.getCanSellerView())

        .type(post.getType())
        .title(post.getTitle())
        .content(post.getContent())

        .build();
  }
}
