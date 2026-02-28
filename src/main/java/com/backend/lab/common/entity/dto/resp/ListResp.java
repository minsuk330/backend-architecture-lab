package com.backend.lab.common.entity.dto.resp;

import java.util.List;
import lombok.Getter;

@Getter
public class ListResp<T> {

  private Integer size;
  private List<T> data;

  public ListResp(List<T> data) {
    this.data = data;
    this.size = data != null ? data.size() : 0;
  }
}
