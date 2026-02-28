package com.backend.lab.domain.print.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.print.entity.Print;
import com.backend.lab.domain.print.entity.dto.req.PrintUpdateReq;
import com.backend.lab.domain.print.entity.vo.PrintType;
import com.backend.lab.domain.print.repository.PrintRepository;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrintService {

  private final PrintRepository printRepository;
  private final UploadFileService uploadFileService;

  //관리자 공통
  @Transactional
  public Print ensureAdminPrintExist() {
    return printRepository.findByPrintTypeAndAgentIdIsNull(PrintType.ADMIN)
        .orElseGet(this::createDefaultAdminPrint);
  }


  private Print createDefaultAdminPrint() {
    return printRepository.save(
        Print.builder()
            .agentId(null)
            .printType(PrintType.ADMIN)
            .build()
    );
  }

  //각 공인중개사별
  @Transactional
  public Print ensureAgentPrintExist(Long agentId) {
    return printRepository.findByPrintTypeAndAgentId(PrintType.AGENT,agentId)
        .orElseGet(() -> createDefaultAgentPrint(agentId));
  }

  private Print createDefaultAgentPrint(Long agentId) {
    UploadFile firstImage = uploadFileService.getById(11366L);
    UploadFile lastImage = uploadFileService.getById(11437L);
    return printRepository.save(
        Print.builder()
            .lastPageMainTitle("현명한 투자의 시작을 함께하겠습니다")
            .lastPageSubTitle("당신의 성공을 응원합니다")
            .firstPageImages(List.of(firstImage))
            .lastPageImages(List.of(lastImage))
            .lastPageMainColor("#000000")
            .lastPageSubColor("#000000")
            .agentId(agentId)
            .printType(PrintType.AGENT)
            .build()
    );
  }

  //무조건 초기에 존재한다는 가정
  @Transactional(readOnly = true)
  public Print getPrint(PrintType printType, Long agentId) {
    if (printType == PrintType.ADMIN) {
      return printRepository.findByPrintType(PrintType.ADMIN)
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Not Found Admin Print"));
    } else {
      return printRepository.findByPrintTypeAndAgentId(PrintType.AGENT, agentId)
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Not Found Agent Print"));
    }
  }

  @Transactional
  public void updateImages(PrintUpdateReq req, PrintType printType, Long agentId)  {
    Print print;
    if (printType == PrintType.ADMIN) {
      print = ensureAdminPrintExist();

    }
    else {
      print = ensureAgentPrintExist(agentId);
    }
    if(req.getFirstPageImages()!=null) {
      // 삭제할 파일들 찾기
      Set<UploadFile> newSet = new HashSet<>(req.getFirstPageImages()); //새로 업데이트된 이미지
      List<UploadFile> toDelete = print.getFirstPageImages().stream() //수정시 이미 기존에 존재하는 이미지를 제외한 나머지
          .filter(old -> !newSet.contains(old))
          .toList();

      // 순서 보장하며 재구성
      print.getFirstPageImages().clear();
      printRepository.saveAndFlush(print);

      // 새로운 순서 그대로 추가
      print.getFirstPageImages().addAll(req.getFirstPageImages());

      // 파일 삭제 기본 이미지 제외
      toDelete.stream()
          .filter(file -> !List.of(11366L, 11437L).contains(file.getId()))
          .forEach(uploadFileService::delete); //더이상 사용하지 않는 것들 삭제
    }
    if(req.getLastPageImages()!=null) {
      Set<UploadFile> newSet = new HashSet<>(req.getLastPageImages()); //새로 업데이트된 이미지
      List<UploadFile> toDelete = print.getLastPageImages().stream() //수정시 이미 기존에 존재하는 이미지를 제외한 나머지
          .filter(old -> !newSet.contains(old))
          .toList();

      // 순서 보장하며 재구성
      print.getLastPageImages().clear();
      printRepository.saveAndFlush(print);

      print.getLastPageImages().addAll(req.getLastPageImages());

      // 파일 삭제 기본 이미지 제외
      toDelete.stream()
          .filter(file -> !List.of(11366L, 11437L).contains(file.getId()))
          .forEach(uploadFileService::delete); //더이상 사용하지 않는 것들 삭제
    }
  }

  @Transactional
  public void updateLastTitle(PrintUpdateReq req, PrintType printType, Long agentId) {
    Print print;
    if (printType == PrintType.ADMIN) {
      print = ensureAdminPrintExist();
    }
    else {
      print = ensureAgentPrintExist(agentId);
    }
    if(req.getLastPageMainTitle()!=null) {
      print.setLastPageMainTitle(req.getLastPageMainTitle());
    }
    if(req.getLastPageSubTitle()!=null) {
      print.setLastPageSubTitle(req.getLastPageSubTitle());
    }
    if(req.getLastPageSubColor()!=null) {
      print.setLastPageSubColor(req.getLastPageSubColor());
    }
    if(req.getLastPageMainColor()!=null) {
      print.setLastPageMainColor(req.getLastPageMainColor());
    }
  }



}
