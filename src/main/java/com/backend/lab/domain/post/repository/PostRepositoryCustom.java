package com.backend.lab.domain.post.repository;

import com.backend.lab.domain.post.entity.Post;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.vo.PostViewType;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {

  Page<Post> search(SearchPostOptions options, PostViewType viewType);
}
