package com.jocoos.mybeautip.support.mail;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final MessageService messageService;
    @Value("${mybeautip.smtp.mail}")
    private String from;

    public MailService(JavaMailSender mailSender,
                       MessageService messageService) {
        this.mailSender = mailSender;
        this.messageService = messageService;
    }

    public void sendMessageForPasswordReset(String emailReceiver, String newPassword, String lang) {
        try {
            String senderName = messageService.getMessage("email.sender.name", lang);
            String subject = messageService.getMessage("billing.password_reset.subject", lang);
            String content = makeContent(newPassword);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(from, senderName);
            helper.setTo(emailReceiver);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.info("Failed: {}", e.getMessage());
            throw new BadRequestException("billing_failed_password_reset", "could not send email for password reset");
        }
    }

    private String makeContent(String newPassword) throws IOException {
        String template = loadFromFile();
        return template.replace("PASSWORD", newPassword);
    }

    private String loadFromFile() throws IOException {
        Resource resource = new ClassPathResource("classpath:templates/password-reset.txt");
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        return new String(bdata, StandardCharsets.UTF_8);
    }
}
