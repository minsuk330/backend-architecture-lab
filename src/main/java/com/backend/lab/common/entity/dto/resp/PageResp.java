package com.backend.lab.common.entity.dto.resp;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResp<T> {

  private int page;
  private int size;
  private int totalPage;
  private long total;
  private List<T> data;

  public PageResp(Page<?> page, List<T> data) {
    this.page = page.getNumber();
    this.size = page.getSize();
    this.totalPage = page.getTotalPages();
    this.total = page.getTotalElements();
    this.data = data;
  }

  public PageResp(int page, int size, int totalPage, long total, List<T> data) {
    this.page = page;
    this.size = size;
    this.totalPage = totalPage;
    this.total = total;
    this.data = data;
  }
}
