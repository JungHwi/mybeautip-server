package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.account.Account;
import com.jocoos.mybeautip.member.account.AccountRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.VbankResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/account")
public class AccountController {

  private final MemberService memberService;
  private final MessageService messageService;
  private final IamportService iamportService;
  private final AccountRepository accountRepository;

  private static final String ACCOUNT_NOT_FOUND = "account.not_found";
  private static final String ACCOUNT_BANK_NOT_SUPPORTED = "account.not_supported_bank_code";
  private static final String ACCOUNT_INVALID_INFO = "account.invalid_info";
  private static final String ACCOUNT_INVALID_BANK_CODE = "account.invalid_bank_code";
  
  private static final String BANK_CODE_ETC = "000";
  
  private final Map<String, String> bankCodeMap = new HashMap<String, String>() {
    {
      put("004", "KB국민은행");
      put("023", "SC제일은행");
      put("039", "경남은행");
      put("034", "광주은행");
      put("003", "기업은행");
      put("011", "농협");
      put("031", "대구은행");
      put("032", "부산은행");
      put("007", "수협");
      put("088", "신한은행");
      put("005", "외환은행");
      put("020", "우리은행");
      put("071", "우체국");
      put("037", "전북은행");
      put("012", "축협");
      put("081", "하나은행(서울은행)");
      put("027", "한국씨티은행(한미은행)");
      put("089", "K뱅크");
      put("090", "카카오뱅크");
      put(BANK_CODE_ETC, "");
    }
  };

  public AccountController(MemberService memberService,
                           MessageService messageService,
                           IamportService iamportService,
                           AccountRepository accountRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
    this.iamportService = iamportService;
    this.accountRepository = accountRepository;
  }

  @GetMapping
  public ResponseEntity<AccountInfo> getAccount(@RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Long memberId = memberService.currentMemberId();
    return accountRepository.findById(memberId)
       .map(account -> {
         log.debug("account: {}", account);
         AccountInfo accountInfo = new AccountInfo();
         BeanUtils.copyProperties(account, accountInfo);
         log.debug("accountInfo: {}", accountInfo);

         return new ResponseEntity<>(accountInfo, HttpStatus.OK);
       }).orElseThrow(() -> new NotFoundException("account_not_found", messageService.getMessage(ACCOUNT_NOT_FOUND, lang)));
  }

  @PatchMapping
  public ResponseEntity<AccountInfo> saveAccount(@Valid @RequestBody UpdateAccountInfo updateAccountInfo,
                                                 @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang,
                                                 BindingResult bindingResult) {

    log.debug("updateAccountInfo: {}", updateAccountInfo);

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }
  
    memberService.checkEmailValidation(updateAccountInfo.getEmail(), lang);
    boolean valid = validAccount(updateAccountInfo, lang);

    Long memberId = memberService.currentMemberId();
    return accountRepository.findById(memberId)
       .map(account -> {
         account.setMemberId(memberId);
         account.setValidity(valid);
         return saveOrUpdate(updateAccountInfo, account);
       })
       .orElseGet(() -> saveOrUpdate(updateAccountInfo, new Account(memberId, valid)));
  }

  @Transactional
  private ResponseEntity<AccountInfo> saveOrUpdate(UpdateAccountInfo updateAccountInfo, Account account) {
    if (updateAccountInfo != null) {
      BeanUtils.copyProperties(updateAccountInfo, account);
    }

    accountRepository.save(account);
    log.debug("account: {}", account);

    AccountInfo accountInfo = new AccountInfo();
    BeanUtils.copyProperties(account, accountInfo);
    log.debug("accountInfo: {}", accountInfo);

    return new ResponseEntity<>(accountInfo, HttpStatus.OK);
  }
  
  private boolean validAccount(UpdateAccountInfo info, String lang) {
    if (BANK_CODE_ETC.equals(info.getBankCode())) {
      return false;
    }
    
    if (!bankCodeMap.containsKey(info.getBankCode())) {  // Not supported bank code
      throw new BadRequestException("not_supported_bank_code", messageService.getMessage(ACCOUNT_BANK_NOT_SUPPORTED, lang));
    }
  
    if (!info.getBankName().equals(bankCodeMap.get(info.getBankCode()))) {  // BadRequest, bank code and name does not match
      throw new BadRequestException("invalid_bank_code", messageService.getMessage(ACCOUNT_INVALID_BANK_CODE, lang));
    }
    
    try {
      ResponseEntity<VbankResponse> response = iamportService.validAccountInfo(info);
      log.debug("{}, {}", response.getStatusCode(), response.getBody());

      if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getCode() == 0) {
        if (info.getBankDepositor().equalsIgnoreCase(response.getBody().getResponse().getBankHolder())) {
          return true;
        } else {
          throw new BadRequestException("invalid_account", messageService.getMessage(ACCOUNT_INVALID_INFO, lang));
        }
      }
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new BadRequestException("invalid_account", messageService.getMessage(ACCOUNT_INVALID_INFO, lang));
      } else {
        log.warn("invalid_iamport_response, GET /vbank/holder API returns exception", e.getStatusCode());
        return false;
      }
    }
    return false;
  }
  
  
  @Data
  @NoArgsConstructor
  public static class UpdateAccountInfo {

    @NotNull @Size(max = 50)
    private String email;

    @NotNull @Size(max = 50)
    private String bankName;
  
    @NotNull @Size(max = 3)
    private String bankCode;

    @NotNull @Size(max = 50)
    private String bankAccount;

    @NotNull @Size(max = 50)
    private String bankDepositor;
  }

  @Data
  @NoArgsConstructor
  public static class AccountInfo {
    private Long memberId;
    private String email;
    private String bankName;
    private String bankCode;
    private String bankAccount;
    private String bankDepositor;
    private Boolean validity;
    private Date createdAt;
  }
}
