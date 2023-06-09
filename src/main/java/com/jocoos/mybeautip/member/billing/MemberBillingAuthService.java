package com.jocoos.mybeautip.member.billing;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.BillingController;
import com.jocoos.mybeautip.support.RandomUtils;
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
import java.util.Optional;

@Slf4j
@Service
public class MemberBillingAuthService {
    // encrypt/decrypt password
    private static final int APPEND_LEN = 4;
    private final String billingAuthKey = "mybeautip-anrWlQk-qlffld@@123";
    private final MemberBillingAuthRepository memberBillingAuthRepository;
    private final MessageService messageService;
    private final MailService mailService;
    @Value("${mybeautip.billing.time-between-reset}")
    private long timeBetweenReset;
    @Value("${mybeautip.billing.max-error-count}")
    private long maxErrorCount;

    public MemberBillingAuthService(MemberBillingAuthRepository memberBillingAuthRepository,
                                    MessageService messageService,
                                    MailService mailService) {
        this.memberBillingAuthRepository = memberBillingAuthRepository;
        this.messageService = messageService;
        this.mailService = mailService;
    }

    public MemberBillingAuth create(Long memberId, BillingController.CreateBillingAuthRequest request) {
        Optional<MemberBillingAuth> optionalBillingAuth = memberBillingAuthRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId);
        if (optionalBillingAuth.isPresent()) {
            return optionalBillingAuth.get();
        } else {
            MemberBillingAuth billingAuth = new MemberBillingAuth();
            billingAuth.setMemberId(memberId);
            billingAuth.setUsername(request.getUsername());
            billingAuth.setEmail(request.getEmail());

            MemberBillingService.EncryptedInfo encryptedInfo = encrypt(request.getPassword());
            billingAuth.setPassword(encryptedInfo.encrypted);
            billingAuth.setSalt(encryptedInfo.salt);
            return memberBillingAuthRepository.save(billingAuth);
        }
    }

    public MemberBillingAuth update(Long memberId, BillingController.UpdateBillingAuthRequest request, String lang) {
        MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.auth.not_found", lang);
                    return new BadRequestException(message);
                });

        MemberBillingService.EncryptedInfo encryptedInfo = encrypt(request.getPassword());
        billingAuth.setPassword(encryptedInfo.encrypted);
        billingAuth.setSalt(encryptedInfo.salt);

        return memberBillingAuthRepository.save(billingAuth);
    }

    public MemberBillingAuth get(Long memberId, String lang) {
        return memberBillingAuthRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.auth.not_found", lang);
                    return new BadRequestException(message);
                });
    }

    @Transactional
    public void remove(Long memberId) {
        memberBillingAuthRepository.deleteTopByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional
    public MemberBillingAuth auth(Long memberId, BillingController.BillingAuthRequest request, String lang) {
        MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.auth.not_found", lang);
                    return new BadRequestException(message);
                });

        String decrypted = decrypt(billingAuth.getPassword(), billingAuth.getSalt());
        boolean matched = decrypted.equals(request.getPassword());
        if (matched) {
            billingAuth.setErrorCount(0);
        } else {
            // check how many times user can fail to authenticate password?
            if (billingAuth.getErrorCount() > maxErrorCount) {
                String message = messageService.getMessage("billing.password.too_many_errors", lang);
                throw new BadRequestException(message);
            }
            billingAuth.setErrorCount(billingAuth.getErrorCount() + 1);
        }
        return memberBillingAuthRepository.save(billingAuth);
    }

    @Async
    public void resetPasswordAsync(Long memberId, String lang) {
        MemberBillingAuth billingAuth = memberBillingAuthRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.auth.not_found", lang);
                    return new BadRequestException(message);
                });

        // check reset time
        long timeWall = billingAuth.getResetAt().getTime() + timeBetweenReset;
        if (System.currentTimeMillis() < timeWall) {
            String message = messageService.getMessage("billing.password.too_many_resets", lang);
            throw new BadRequestException(message);
        }

        // send email async
        log.info("send email: to = {}", billingAuth.getEmail());
        String newPassword = String.valueOf(RandomUtils.getRandom(111111, 999999));
        mailService.sendMessageForPasswordReset(billingAuth.getEmail(), newPassword, lang);

        // update password
        MemberBillingService.EncryptedInfo encryptedInfo = encrypt(newPassword);
        billingAuth.setPassword(encryptedInfo.encrypted);
        billingAuth.setSalt(encryptedInfo.salt);
        billingAuth.setResetAt(new Date());
        memberBillingAuthRepository.save(billingAuth);
    }

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


}
