package com.backend.lab.common.entity.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;


@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PageOptions {

  private final static int DEFAULT_PAGE_SIZE = 10;
  private final static int DEFAULT_PAGE_NUMBER = 0;

  private Integer page;
  private Integer size;

  public Pageable pageable() {
    int pageNumber = (page != null) ? page : DEFAULT_PAGE_NUMBER;
    int pageSize = (size != null) ? size : DEFAULT_PAGE_SIZE;
    return Pageable.ofSize(pageSize).withPage(pageNumber);
  }
}
