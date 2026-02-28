package com.backend.lab.domain.popup.repository;

import com.backend.lab.domain.popup.entity.Popup;
import com.backend.lab.domain.popup.entity.dto.req.SearchPopupOptions;
import com.backend.lab.domain.popup.entity.vo.PopupViewType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface PopupRepositoryCustom {

  Page<Popup> search(SearchPopupOptions options, PopupViewType viewType);
  List<Popup> getsAvailable(LocalDate date, PopupViewType viewType);
}
