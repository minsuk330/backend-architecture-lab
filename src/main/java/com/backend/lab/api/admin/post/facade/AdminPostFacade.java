package com.backend.lab.api.admin.post.facade;

import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.post.entity.Post;
import com.backend.lab.domain.post.entity.dto.req.PostCreateReq;
import com.backend.lab.domain.post.entity.dto.req.PostUpdateReq;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.dto.resp.PostResp;
import com.backend.lab.domain.post.entity.vo.PostViewType;
import com.backend.lab.domain.post.service.PostService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostFacade {

  private final AdminService adminService;
  private final UploadFileService uploadFileService;
  private final PostService postService;

  private final PostViewType viewType = PostViewType.ALL;

  @Transactional(readOnly = true)
  public PostResp getById(Long postId) {
    Post post = postService.getById(postId);
    return this.postResp(post);
  }

  @Transactional(readOnly = true)
  public PageResp<PostResp> search(SearchPostOptions options) {
    Page<Post> page = postService.search(options, viewType);

    List<PostResp> data = page.getContent().stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(Post::getCreatedAt).reversed()))
        .map(this::postResp)
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional
  public void create(Long adminId, PostCreateReq req) {
    Admin admin = adminService.getById(adminId);
    postService.create(admin, req);
  }

  @Transactional
  public void update(Long postId, PostUpdateReq req) {
    postService.update(postId, req);
  }

  @Transactional
  public void delete(Long postId) {
    postService.delete(postId);
  }

  public PostResp postResp(Post post) {
    try {
      Admin admin = post.getCreatedBy();
      return postService.postResp(post, adminService.adminResp(admin, uploadFileService.uploadFileResp(admin != null ? admin.getProfileImage() : null)));
    } catch (Exception e) {
      return postService.postResp(post, adminService.adminResp(null, null));
    }
  }
}
