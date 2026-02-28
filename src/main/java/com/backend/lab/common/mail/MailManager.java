package com.backend.lab.common.mail;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.mail.dto.req.MailSendReq;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailManager {

  private final JavaMailSender javaMailSender;

  public void sendResetPasswordMail(String email, String password) {
    MailSendReq req = MailSendReq.builder()
        .to(email)
        .subject(getResetPasswordMailSubject())
        .content(getResetPasswordMailContent(password))
        .build();
    this.sendMail(req);
  }

  public void sendMail(MailSendReq req) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

      helper.setTo(req.getTo());
      helper.setSubject(req.getSubject());
      helper.setText(req.getContent(), true); // 두 번째 파라미터가 HTML 여부

      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
    }

  }

  public String getResetPasswordMailContent(String password) {
    return String.format(
        "<h3>임시 비밀번호 발급</h3>"
            + "<p>안녕하세요, 모두의 건물입니다.</p>"
            + "<p>요청하신 임시 비밀번호는 <strong>%s</strong> 입니다.</p>"
            + "<p>로그인 후 반드시 비밀번호를 변경해 주세요.</p>"
            + "<p>감사합니다.</p>", password);
  }

  public String getResetPasswordMailSubject() {
    return "[모두의 건물] 임시 비밀번호 발급";
  }
}

