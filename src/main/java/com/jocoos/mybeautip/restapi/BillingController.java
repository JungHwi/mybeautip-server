package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberBilling;
import com.jocoos.mybeautip.member.MemberBillingService;
import com.jocoos.mybeautip.member.MemberService;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class BillingController {
  private final MemberService memberService;
  private final MemberBillingService memberBillingService;

  public BillingController(MemberService memberService,
                                 MemberBillingService memberBillingService) {
    this.memberService = memberService;
    this.memberBillingService = memberBillingService;
  }

  @GetMapping("/billings")
  public ResponseEntity<List<BillingInfo>> list() {
    Member member = memberService.currentMember();
    List<BillingInfo> result = memberBillingService.getCards(member.getId()).stream()
        .map(BillingInfo::new).collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/billings")
  public ResponseEntity<LinkInfo> create() {
    Member member = memberService.currentMember();
    MemberBilling memberBilling = memberBillingService.createCustomerId(member.getId());
    String customerId = memberBillingService.getCustomerId(memberBilling);
    return new ResponseEntity<>(new LinkInfo(memberBilling.getId(), customerId), HttpStatus.OK);
  }

  @DeleteMapping("/billings/{id}")
  public ResponseEntity<BillingInfo> delete(@PathVariable Long id) {
    Member member = memberService.currentMember();
    memberBillingService.deleteBillingInfo(member.getId(), id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/billings/{id}/complete")
  public ResponseEntity<BillingInfo> complete(@PathVariable Long id) {
    Member member = memberService.currentMember();
    MemberBilling mb = memberBillingService.completeBillingInfo(member.getId(), id);
    return new ResponseEntity<>(new BillingInfo(mb), HttpStatus.OK);
  }

  @PatchMapping("/billings/{id}")
  public ResponseEntity<BillingInfo> update(@PathVariable Long id,
                                            @Valid @RequestBody UpdateBillingRequest request) {
    Member member = memberService.currentMember();

    if (request.base) {
      MemberBilling memberBilling = memberBillingService.updateBillingToBase(member.getId(), id);
      return new ResponseEntity<>(new BillingInfo(memberBilling), HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/billings/base")
  public ResponseEntity<BillingInfo> getBaseBilling() {
    Member member = memberService.currentMember();
    MemberBilling mb = memberBillingService.getBaseBillingInfo(member.getId());
    return new ResponseEntity<>(new BillingInfo(mb), HttpStatus.OK);
  }

  @Data
  public static class UpdateBillingRequest {
    @NotNull
    private Boolean base;
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
}
