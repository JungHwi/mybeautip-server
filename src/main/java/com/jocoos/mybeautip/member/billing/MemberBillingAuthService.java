package com.jocoos.mybeautip.member.billing;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.BillingController;
import com.jocoos.mybeautip.support.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class MemberBillingAuthService {
  @Value("${mybeautip.billing.time-between-reset}")
  private long timeBetweenReset;

  @Value("${mybeautip.billing.max-error-count}")
  private long maxErrorCount;

  private final String billingAuthKey = "mybeautip-anrWlQk-qlffld@@123";

  private final MemberBillingAuthRepository memberBillingAuthRepository;
  private final MessageService messageService;
  private final MailService mailService;

  public MemberBillingAuthService(MemberBillingAuthRepository memberBillingAuthRepository,
                                  MessageService messageService,
                                  MailService mailService) {
    this.memberBillingAuthRepository = memberBillingAuthRepository;
    this.messageService = messageService;
    this.mailService = mailService;
  }

  public MemberBillingAuth create(Long memberId, BillingController.CreateBillingAuthRequest request)  {
    MemberBillingAuth billingAuth = new MemberBillingAuth();
    billingAuth.setMemberId(memberId);
    billingAuth.setUsername(request.getUsername());
    billingAuth.setEmail(request.getEmail());

    MemberBillingService.EncryptedInfo encryptedInfo = encrypt(request.getPassword());
    billingAuth.setPassword(encryptedInfo.encrypted);
    billingAuth.setSalt(encryptedInfo.salt);

    return memberBillingAuthRepository.save(billingAuth);
  }

  public MemberBillingAuth update(Long memberId, BillingController.UpdateBillingAuthRequest request, String lang) {
    MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberId(memberId)
        .orElseThrow(() -> {
          String message = messageService.getMessage("billing.auth.not_found", lang);
          return new BadRequestException("billing_auth_not_found", message);
        });

    MemberBillingService.EncryptedInfo encryptedInfo = encrypt(request.getPassword());
    billingAuth.setPassword(encryptedInfo.encrypted);
    billingAuth.setSalt(encryptedInfo.salt);

    return memberBillingAuthRepository.save(billingAuth);
  }

  public MemberBillingAuth get(Long memberId, String lang) {
    return memberBillingAuthRepository.findTopByMemberId(memberId)
        .orElseThrow(() -> {
          String message = messageService.getMessage("billing.auth.not_found", lang);
          return new BadRequestException("billing_auth_not_found", message);
        });
  }

  @Transactional
  public MemberBillingAuth auth(Long memberId, BillingController.BillingAuthRequest request, String lang) {
    MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberId(memberId)
        .orElseThrow(() -> {
          String message = messageService.getMessage("billing.auth.not_found", lang);
          return new BadRequestException("billing_auth_not_found", message);
        });

    String decrypted = decrypt(billingAuth.getPassword(), billingAuth.getSalt());
    boolean matched = decrypted.equals(request.getPassword());
    if (matched) {
      billingAuth.setErrorCount(0);
    } else {
      // check how many times user can fail to authenticate password?
      if (billingAuth.getErrorCount() > maxErrorCount) {
        String message = messageService.getMessage("billing.password.too_many_errors", lang);
        throw new BadRequestException("billing_password_too_many_errors", message);
      }
      billingAuth.setErrorCount(billingAuth.getErrorCount() + 1);
    }
    return memberBillingAuthRepository.save(billingAuth);
  }

  @Async
  public void resetPasswordAsync(Long memberId, String lang) {
    MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberId(memberId)
        .orElseThrow(() -> {
          String message = messageService.getMessage("billing.auth.not_found", lang);
          return new BadRequestException("billing_auth_not_found", message);
        });

    // check reset time
    long timeWall = billingAuth.getResetAt().getTime() + timeBetweenReset;
    if (System.currentTimeMillis() < timeWall) {
      String message = messageService.getMessage("billing.password.too_many_resets", lang);
      throw new BadRequestException("billing_password_too_many_resets", message);
    }

    // send email async
    log.info("send email: to = {}", billingAuth.getEmail());
    String newPassword = String.valueOf(getRandomIntegerBetweenRange(111111, 999999));
    mailService.sendMessageForPasswordReset(billingAuth.getEmail(), newPassword, lang);

    // update password
    MemberBillingService.EncryptedInfo encryptedInfo = encrypt(newPassword);
    billingAuth.setPassword(encryptedInfo.encrypted);
    billingAuth.setSalt(encryptedInfo.salt);
    billingAuth.setResetAt(new Date());
    memberBillingAuthRepository.save(billingAuth);
  }

  // encrypt/decrypt password
  private static final int APPEND_LEN = 4;

  private MemberBillingService.EncryptedInfo encrypt(String password) {
    return encrypt(password, KeyGenerators.string().generateKey());
  }

  /**
   * append 4 characters in front of salt for security
   */
  private MemberBillingService.EncryptedInfo encrypt(String password, String salt) {
    String append = KeyGenerators.string().generateKey().substring(0, APPEND_LEN);
    TextEncryptor encryptor = Encryptors.text(billingAuthKey, salt);
    return new MemberBillingService.EncryptedInfo(encryptor.encrypt(password), append + salt);
  }

  private String decrypt(String password, String salt) {
    String realSalt = salt.substring(APPEND_LEN);
    TextEncryptor decryptor = Encryptors.text(billingAuthKey, realSalt);
    return decryptor.decrypt(password);
  }

  public static int getRandomIntegerBetweenRange(double min, double max){
    double x = (int)(Math.random()*((max-min)+1)) + min;
    return (int)x;
  }
}
