package com.jocoos.mybeautip.member.billing;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentBillingInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberBillingService {
    public final static String MERCHANT_BILLING_PREFIX = "mybeautip_billing_";
    private final IamportService iamportService;
    private final MessageService messageService;
    private final MemberBillingRepository memberBillingRepository;
    private final String billingKey = "mybeautip-akdlqbxlq-Qkd9Ehd9";

    public MemberBillingService(IamportService iamportService,
                                MessageService messageService,
                                MemberBillingRepository memberBillingRepository) {
        this.iamportService = iamportService;
        this.messageService = messageService;
        this.memberBillingRepository = memberBillingRepository;
    }

    @Transactional
    public MemberBilling getBaseBillingInfo(Long memberId, String lang) {
        // try to find base billing info
        Optional<MemberBilling> baseInfo = memberBillingRepository.findTopByMemberIdAndValidIsTrueAndBaseIsTrue(memberId);
        if (!baseInfo.isPresent()) {
            List<MemberBilling> validInfos = memberBillingRepository.findByMemberIdAndValid(memberId, true);
            if (validInfos.isEmpty()) {
                // no valid billing infos
                String message = messageService.getMessage("billing.not_found", lang);
                throw new BadRequestException("billing_not_found", message);
            } else {
                // there are valid billing infos which is not base
                MemberBilling validInfo = validInfos.get(0);
                validInfo.setBase(true);
                memberBillingRepository.save(validInfo);
                return validInfo;
            }
        }
        return baseInfo.get();
    }

    public MemberBilling createCustomerId(Long memberId) {
        // use previously created one which is not validated
        Optional<MemberBilling> memberBillings = memberBillingRepository.findTopByMemberIdAndValidIsFalse(memberId);
        if (memberBillings.isPresent()) {
            return memberBillings.get();
        }

        // create new customer id
        // TODO: update random unique value later?
        String customerId = "mybeautip_" + KeyGenerators.string().generateKey() + ":" + System.currentTimeMillis();
        EncryptedInfo encryptedInfo = encrypt(customerId);

        MemberBilling mb = new MemberBilling();
        mb.setBase(false);
        mb.setCustomerId(encryptedInfo.encrypted);
        mb.setSalt(encryptedInfo.salt);
        mb.setMemberId(memberId);
        mb.setValid(false);
        return memberBillingRepository.save(mb);
    }

    @Transactional
    public MemberBilling completeBillingInfo(Long memberId, Long billingId, String lang) {
        MemberBilling memberBilling = memberBillingRepository.findByIdAndMemberIdAndValid(billingId, memberId, false)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.not_found", lang);
                    return new BadRequestException("billing_not_found", message);
                });

        // fetch card info from iamport
        String customerId = getCustomerId(memberBilling);
        String token = iamportService.getToken();
        PaymentBillingInfoData response = iamportService.getCardInfo(token, customerId, billingId);

        resetBaseBilling(memberId);

        // set billing info completed to base and insert card info
        memberBilling.setBase(true);
        memberBilling.setCardName(response.getCardName());
        memberBilling.setCardNumber(response.getCardNumber());
        memberBilling.setValid(true);
        return memberBillingRepository.save(memberBilling);
    }

    public List<MemberBilling> getCards(Long memberId) {
        return memberBillingRepository.findByMemberIdAndValid(memberId, true);
    }

    @Transactional
    public MemberBilling updateBillingToBase(Long memberId, Long billingId, String lang) {
        resetBaseBilling(memberId);

        MemberBilling memberBilling = memberBillingRepository.findByIdAndMemberIdAndValid(billingId, memberId, true)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.not_found", lang);
                    return new BadRequestException("billing_not_found", message);
                });
        memberBilling.setBase(true);
        return memberBillingRepository.save(memberBilling);
    }

    private void resetBaseBilling(Long memberId) {
        // unset previous all base billing : it should be one item actually
        List<MemberBilling> memberBillings = memberBillingRepository.findByMemberIdAndBaseIsTrue(memberId);
        for (MemberBilling mb : memberBillings) {
            mb.setBase(false);
        }
        memberBillingRepository.saveAll(memberBillings);
    }

    public void deleteBillingInfo(Long memberId, Long billingId, String lang) {
        // try to find billing info to delete
        MemberBilling memberBilling = memberBillingRepository.findByIdAndMemberIdAndValid(billingId, memberId, true)
                .orElseThrow(() -> {
                    String message = messageService.getMessage("billing.not_found", lang);
                    return new BadRequestException("billing_not_found", message);
                });

        String customerId = decrypt(memberBilling.getCustomerId(), memberBilling.getSalt());

        // remove billing info in both iamport and mybeautip db
        String token = iamportService.getToken();
        iamportService.removeBillingInfo(token, customerId, memberBilling.getId());

        memberBillingRepository.delete(memberBilling);
    }

    public String getCustomerId(MemberBilling memberBilling) {
        return decrypt(memberBilling.getCustomerId(), memberBilling.getSalt());
    }

    // encrypt/decrypt customer id
    private EncryptedInfo encrypt(String customerId) {
        return encrypt(customerId, KeyGenerators.string().generateKey());
    }

    private EncryptedInfo encrypt(String customerId, String salt) {
        TextEncryptor encryptor = Encryptors.text(billingKey, salt);
        return new EncryptedInfo(encryptor.encrypt(customerId), salt);
    }

    private String decrypt(String customerId, String salt) {
        TextEncryptor decryptor = Encryptors.text(billingKey, salt);
        return decryptor.decrypt(customerId);
    }

    public static class EncryptedInfo {
        String encrypted;
        String salt;

        EncryptedInfo(String encrypted, String salt) {
            this.encrypted = encrypted;
            this.salt = salt;
        }
    }
}
