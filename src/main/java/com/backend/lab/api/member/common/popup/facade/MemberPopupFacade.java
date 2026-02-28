package com.backend.lab.api.member.common.popup.facade;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.popup.entity.Popup;
import com.backend.lab.domain.popup.entity.dto.resp.PopupResp;
import com.backend.lab.domain.popup.entity.vo.PopupViewType;
import com.backend.lab.domain.popup.service.PopupService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberPopupFacade {

  private final PopupService popupService;
  private final MemberService memberService;
  private final UploadFileService uploadFileService;

  @Transactional(readOnly = true)
  public ListResp<PopupResp> gets(Long memberId) {
    PopupViewType viewType;

    if (memberId == null) {
      viewType = PopupViewType.ANONYMOUS;
    } else {
      Member member = memberService.getById(memberId);
      viewType = switch (member.getType()) {
        case AGENT -> PopupViewType.AGENT;
        case BUYER -> PopupViewType.BUYER;
        case SELLER -> PopupViewType.SELLER;
        default -> throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
      };
    }

    List<Popup> rows = popupService.getsAvailable(LocalDate.now(), viewType);
    List<PopupResp> data = rows.stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(Popup::getCreatedAt).reversed()))
        .map(this::popupResp)
        .toList();
    return new ListResp<>(data);
  }

  public PopupResp popupResp(Popup popup) {
    return popupService.popupResp(
        popup,
        null,
        uploadFileService.uploadFileResp(popup.getImage())
    );
  }
}
