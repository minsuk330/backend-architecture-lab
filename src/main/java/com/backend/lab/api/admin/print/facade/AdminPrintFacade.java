package com.backend.lab.api.admin.print.facade;

import com.backend.lab.domain.print.entity.Print;
import com.backend.lab.domain.print.entity.dto.req.PrintUpdateReq;
import com.backend.lab.domain.print.entity.dto.resp.PrintResp;
import com.backend.lab.domain.print.entity.vo.PrintType;
import com.backend.lab.domain.print.service.PrintService;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPrintFacade {

  private final PrintService printService;
  private final UploadFileService uploadFileService;

  @Transactional
  public PrintResp get () {
    Print print = printService.ensureAdminPrintExist();
    return this.printResp(print);
  }

  @Transactional
  public void imageUpdate (PrintUpdateReq req) {
    if (req.getFirstPageImageIds() != null && !req.getFirstPageImageIds().isEmpty()) {
      List<UploadFile> firstImages = req.getFirstPageImageIds().stream()
          .map(uploadFileService::getById)
          .toList();
      req.setFirstPageImages(firstImages);
    }
    if (req.getLastPageImageIds() != null && !req.getLastPageImageIds().isEmpty()) {
      List<UploadFile> lastImages = req.getLastPageImageIds().stream()
          .map(uploadFileService::getById)
          .toList();
      req.setLastPageImages(lastImages);
    }

    printService.updateImages(req, PrintType.ADMIN,null);
  }
  @Transactional
  public void titleUpdate (PrintUpdateReq req) {
    printService.updateLastTitle(req, PrintType.ADMIN,null);
  }

  public PrintResp printResp(Print print) {
    return PrintResp.builder()
        .firstPageImages(print.getFirstPageImages().stream()
            .map(uploadFileService::uploadFileResp)
            .toList())
        .lastPageImages(print.getLastPageImages().stream()
            .map(uploadFileService::uploadFileResp)
            .toList())
        .lastPageMainTitle(print.getLastPageMainTitle())
        .lastPageMainColor(print.getLastPageMainColor())
        .lastPageSubColor(print.getLastPageSubColor())
        .lastPageSubTitle(print.getLastPageSubTitle())
        .build();
  }
}
