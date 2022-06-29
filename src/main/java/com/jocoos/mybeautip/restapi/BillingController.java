package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.billing.MemberBilling;
import com.jocoos.mybeautip.member.billing.MemberBillingAuth;
import com.jocoos.mybeautip.member.billing.MemberBillingAuthService;
import com.jocoos.mybeautip.member.billing.MemberBillingService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class BillingController {
    private final LegacyMemberService legacyMemberService;
    private final MemberBillingService memberBillingService;
    private final MemberBillingAuthService memberBillingAuthService;

    public BillingController(LegacyMemberService legacyMemberService,
                             MemberBillingService memberBillingService,
                             MemberBillingAuthService memberBillingAuthService) {
        this.legacyMemberService = legacyMemberService;
        this.memberBillingService = memberBillingService;
        this.memberBillingAuthService = memberBillingAuthService;
    }

    @GetMapping("/billings")
    public ResponseEntity<List<BillingInfo>> list() {
        Member member = legacyMemberService.currentMember();
        List<BillingInfo> result = memberBillingService.getCards(member.getId()).stream()
                .map(BillingInfo::new).collect(Collectors.toList());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/billings")
    public ResponseEntity<LinkInfo> create() {
        Member member = legacyMemberService.currentMember();
        MemberBilling memberBilling = memberBillingService.createCustomerId(member.getId());
        String customerId = memberBillingService.getCustomerId(memberBilling);
        return new ResponseEntity<>(new LinkInfo(memberBilling.getId(), customerId), HttpStatus.OK);
    }

    @DeleteMapping("/billings/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        memberBillingAuthService.remove(member.getId());
        memberBillingService.deleteBillingInfo(member.getId(), id, lang);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/billings/{id}/complete")
    public ResponseEntity<BillingInfo> complete(@PathVariable Long id,
                                                @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        MemberBilling mb = memberBillingService.completeBillingInfo(member.getId(), id, lang);
        return new ResponseEntity<>(new BillingInfo(mb), HttpStatus.OK);
    }

    @PatchMapping("/billings/{id}")
    public ResponseEntity<BillingInfo> update(@PathVariable Long id,
                                              @Valid @RequestBody UpdateBillingRequest request,
                                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        if (request.base) {
            MemberBilling memberBilling = memberBillingService.updateBillingToBase(member.getId(), id, lang);
            return new ResponseEntity<>(new BillingInfo(memberBilling), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/billings/base")
    public ResponseEntity<BillingInfo> getBaseBilling(@RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        MemberBilling mb = memberBillingService.getBaseBillingInfo(member.getId(), lang);
        return new ResponseEntity<>(new BillingInfo(mb), HttpStatus.OK);
    }

    @PostMapping("/billings/auth")
    public ResponseEntity<BillingAuthInfo> createAuth(@Valid @RequestBody CreateBillingAuthRequest request) {
        Member member = legacyMemberService.currentMember();

        MemberBillingAuth billingAuth = memberBillingAuthService.create(member.getId(), request);
        return new ResponseEntity<>(new BillingAuthInfo(billingAuth), HttpStatus.OK);
    }

    @PatchMapping("/billings/auth")
    public ResponseEntity<BillingAuthInfo> updateAuth(@Valid @RequestBody UpdateBillingAuthRequest request,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        MemberBillingAuth billingAuth = memberBillingAuthService.update(member.getId(), request, lang);
        return new ResponseEntity<>(new BillingAuthInfo(billingAuth), HttpStatus.OK);
    }

    @GetMapping("/billings/auth")
    public ResponseEntity<BillingAuthInfo> getAuth(@RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();

        MemberBillingAuth billingAuth = memberBillingAuthService.get(member.getId(), lang);
        return new ResponseEntity<>(new BillingAuthInfo(billingAuth), HttpStatus.OK);
    }

    @PostMapping(path = "/billing_auth", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<BillingAuthValidityInfo> auth(@RequestParam Map<String, String> params,
                                                        @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        String pw = params.get("password");
        if (pw == null) {
            throw new BadRequestException("billing_auth_invalid", "password field does not exist");
        }
        Member member = legacyMemberService.currentMember();
        BillingAuthRequest request = new BillingAuthRequest();
        request.setPassword(pw);
        MemberBillingAuth billingAuth = memberBillingAuthService.auth(member.getId(), request, lang);
        return new ResponseEntity<>(new BillingAuthValidityInfo(billingAuth.getErrorCount() == 0, billingAuth), HttpStatus.OK);
    }

    @PostMapping("/billings/auth/reset")
    public ResponseEntity<?> reset(@RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        Member member = legacyMemberService.currentMember();
        memberBillingAuthService.resetPasswordAsync(member.getId(), lang);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Data
    public static class UpdateBillingRequest {
        @NotNull
        private Boolean base;
    }

    @Data
    public static class CreateBillingAuthRequest {
        @NotNull
        private String username;

        @NotNull
        private String email;

        @NotNull
        private String password;
    }

    @Data
    public static class UpdateBillingAuthRequest {
        @NotNull
        private String password;
    }

    @Data
    public static class BillingAuthRequest {
        @NotNull
        private String password;
    }

    @NoArgsConstructor
    @Data
    public static class BillingInfo {
        private Long id;
        private Boolean base;
        private String cardName;
        private String cardNumber;

        public BillingInfo(MemberBilling memberBilling) {
            this.id = memberBilling.getId();
            this.base = memberBilling.getBase();
            this.cardName = memberBilling.getCardName();
            this.cardNumber = memberBilling.getCardNumber();
        }
    }

    @NoArgsConstructor
    @Data
    private static class LinkInfo {
        private Long billingId;
        private String customerId;

        public LinkInfo(Long billingId, String customerId) {
            this.billingId = billingId;
            this.customerId = customerId;
        }
    }

    @NoArgsConstructor
    @Data
    public static class BillingAuthInfo {
        private Long id;
        private String username;
        private String email;

        public BillingAuthInfo(MemberBillingAuth billingAuth) {
            this.id = billingAuth.getId();
            this.username = billingAuth.getUsername();
            this.email = billingAuth.getEmail();
        }
    }

    @NoArgsConstructor
    @Data
    public static class BillingAuthValidityInfo {
        private Boolean validity;
        private Integer errorCount;

        public BillingAuthValidityInfo(boolean validity, MemberBillingAuth billingAuth) {
            this.validity = validity;
            this.errorCount = billingAuth.getErrorCount();
        }
    }
}
