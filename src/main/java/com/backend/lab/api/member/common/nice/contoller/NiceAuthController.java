package com.backend.lab.api.member.common.nice.contoller;

import com.backend.lab.common.nice.NiceAuthFormData;
import com.backend.lab.common.nice.NiceAuthService;
import com.backend.lab.common.nice.dto.NiceAuthResp;
import com.backend.lab.common.nice.store.EncryptInformation;
import com.backend.lab.common.nice.store.NiceSessionManager;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/member/nice")
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 본인인증")
public class NiceAuthController {

  private final NiceAuthService niceAuthService;
  private final NiceSessionManager niceSessionManager;

  @Operation(summary = "본인인증 창 불러오기")
  @GetMapping("/form")
  public String getCryptoFormData(
      @RequestParam(value = "redirectUrl", required = true) String redirectUrl,
      @RequestParam(value = "memberType", required = true) MemberType memberType,
      @RequestParam(value = "email", required = false) String email,
      @RequestParam(value = "password", required = false) String password,
      Model model
  ) {
    NiceAuthFormData niceAuthFormData = niceAuthService.getEncFormData(redirectUrl);
    model.addAttribute("niceAuthFormData", niceAuthFormData);
    niceSessionManager.register(email, password, memberType, niceAuthFormData);
    return "nice";
  }

  @Hidden
  @ResponseBody
  @GetMapping("/callback")
  public RedirectView handleCallback(
      @RequestParam("token_version_id") String tokenVersionId,
      @RequestParam("enc_data") String encData,
      @RequestParam("integrity_value") String integrityValue) {

    EncryptInformation encryptInformation = niceSessionManager.getEncryptInformation(tokenVersionId);
    String email = niceSessionManager.getEmail(tokenVersionId);
    String password = niceSessionManager.getPassword(tokenVersionId);
    MemberType memberType = niceSessionManager.getMemberType(tokenVersionId);
    NiceAuthResp authResp = niceAuthService.handleCallback(tokenVersionId, encData, integrityValue, encryptInformation, email, password, memberType);
    niceSessionManager.invalidate(tokenVersionId);
    return this.redirectView(authResp);
  }

  public RedirectView redirectView(NiceAuthResp resp) {
    String next = resp.getReturnUrl();
    if (!next.contains("/")) {
      next = URLDecoder.decode(resp.getReturnUrl(), StandardCharsets.UTF_8);
    }
    RedirectView redirectView = new RedirectView();
    UriComponentsBuilder nextUriComponent = UriComponentsBuilder.fromUri(URI.create(next))
        .queryParam("success", resp.getSuccess())
        .queryParam("message", URLEncoder.encode(resp.getMessage(), StandardCharsets.UTF_8));

    if (resp.getSuccess()) {
      nextUriComponent.queryParam("di", resp.getDi())
          .queryParam("name", URLEncoder.encode(resp.getName(), StandardCharsets.UTF_8))
          .queryParam("phoneNumber", resp.getPhoneNumber())
          .queryParam("birth", resp.getBirth() != null ? resp.getBirth().toString() : null)
          .queryParam("gender", resp.getGender() != null ? resp.getGender().name() : null)
          .queryParam("email", resp.getEmail())
          .queryParam("password", resp.getPassword());
    }
    String nextUri = nextUriComponent.build().toUriString();

    redirectView.setUrl(nextUri);
    return redirectView;
  }
}