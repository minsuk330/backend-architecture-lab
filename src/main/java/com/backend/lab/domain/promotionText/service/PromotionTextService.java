package com.backend.lab.domain.promotionText.service;

import com.backend.lab.domain.promotionText.entity.PromotionText;
import com.backend.lab.domain.promotionText.entity.PromotionTextField;
import com.backend.lab.domain.promotionText.entity.dto.req.PromotionTextUpdateReq;
import com.backend.lab.domain.promotionText.entity.dto.resp.PromotionTextFieldResp;
import com.backend.lab.domain.promotionText.entity.vo.PromotionMemberType;
import com.backend.lab.domain.promotionText.entity.vo.PromotionPlaceholder;
import com.backend.lab.domain.promotionText.entity.vo.PromotionType;
import com.backend.lab.domain.promotionText.repository.PromotionTextFieldRepository;
import com.backend.lab.domain.promotionText.repository.PromotionTextRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionTextService {

  private final PromotionTextRepository promotionTextRepository;
  private final PromotionTextFieldRepository promotionTextFieldRepository;

  //관리자 공통
  @Transactional
  public PromotionText ensureAdminPromotionExist() {
    return promotionTextRepository.findByMemberType(PromotionMemberType.ADMIN).orElseGet(
        this::createAdminPromotionText);
  }

  public PromotionText createAdminPromotionText() {
    return promotionTextRepository.save(
        PromotionText.builder()
            .memberType(PromotionMemberType.ADMIN)
            .type(PromotionType.SALE)
            .build(
            ));
  }

  @Transactional
  public PromotionText ensureAgentPromotionExist(Long agentId) {
    return promotionTextRepository.findByMemberTypeAndAgentId(PromotionMemberType.AGENT,agentId).orElseGet(()->
        createAgentPromotionText(agentId));
  }

  public PromotionText createAgentPromotionText(Long agentId) {
    return promotionTextRepository.save(
        PromotionText.builder()
            .memberType(PromotionMemberType.AGENT)
            .agentId(agentId)
            .type(PromotionType.SALE)
            .build(
            ));
  }

  @Transactional
  public void update(PromotionTextUpdateReq req, PromotionMemberType memberType, Long agentId) {
    PromotionText promotionText;
    if (memberType == PromotionMemberType.ADMIN) {
      promotionText = ensureAdminPromotionExist();
    } else {
      promotionText = ensureAgentPromotionExist(agentId);
    }

    promotionTextFieldRepository.deleteByPromotionText(promotionText);

    List<PromotionTextFieldResp> fields = parseRawContent(req.getRawContent());


    // 새로운 필드들 추가
    for (PromotionTextFieldResp fieldDto : fields) {
      PromotionTextField field = PromotionTextField.builder()
          .promotionText(promotionText)
          .placeholder(PromotionPlaceholder.of("<" + fieldDto.getPlaceholder() + ">"))
          .seq(fieldDto.getSeq())
          .prefix(fieldDto.getPrefixText())
          .suffix(fieldDto.getSuffixText())
          .build();

      promotionText.addField(field);
    }

    promotionTextRepository.save(promotionText);
  }

  public PromotionTextFieldResp promotionTextFieldResp(PromotionTextField field) {
    return PromotionTextFieldResp.builder()
        .seq(field.getSeq())
        .suffixText(field.getSuffix())
        .prefixText(field.getPrefix())
        .placeholder(field.getPlaceholder().getToken())
        .build();
  }


  private List<PromotionTextFieldResp> parseRawContent(String rawContent) {
    List<PromotionTextFieldResp> result = new ArrayList<>();
    if (rawContent == null || rawContent.isBlank()) {
      return result;
    }

    // \\n을 실제 줄바꿈으로 변환
    String processedContent = rawContent.replace("\\n", "\n");

    Pattern phPattern = Pattern.compile("<([^>]+)>");
    Matcher matcher = phPattern.matcher(processedContent);
    List<MatchResult> matches = matcher.results().toList();

    if (matches.isEmpty()) {
      // 플레이스홀더가 없으면 전체 텍스트를 prefix로 처리
      result.add(PromotionTextFieldResp.builder()
          .seq(1)
          .placeholder("")
          .prefixText(processedContent)
          .suffixText("")
          .build());
      return result;
    }

    int seq = 1;
    int prevEnd = 0;

    for (int idx = 0; idx < matches.size(); idx++) {
      MatchResult cur = matches.get(idx);

      /* ---------- prefix ---------- */
      String prefixText = processedContent.substring(prevEnd, cur.start());

      /* ---------- suffix ---------- */
      int suffixStart = cur.end();
      int suffixEnd;

      if (idx + 1 < matches.size()) {
        // 다음 플레이스홀더가 있는 경우 - 줄바꿈까지만
        int nextLineBreak = processedContent.indexOf('\n', suffixStart);
        int nextPlaceholderStart = matches.get(idx + 1).start();

        if (nextLineBreak != -1 && nextLineBreak < nextPlaceholderStart) {
          suffixEnd = nextLineBreak + 1;
        } else {
          suffixEnd = nextPlaceholderStart;
        }
      } else {
        // 마지막 플레이스홀더인 경우 - 끝까지 포함
        suffixEnd = processedContent.length();
      }

      String suffixText = processedContent.substring(suffixStart, suffixEnd);

      /* ---------- token / DTO build ---------- */
      String token = cur.group(1); // item_id, address, ...

      result.add(PromotionTextFieldResp.builder()
          .seq(seq++)
          .placeholder(token)
          .prefixText(prefixText)
          .suffixText(suffixText)
          .build());

      // 다음 prefix 계산을 위한 prevEnd 설정
      prevEnd = suffixEnd;
    }


    return result;
  }

  }
