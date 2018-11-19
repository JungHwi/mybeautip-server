package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.account.Account;
import com.jocoos.mybeautip.member.account.AccountRepository;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/account")
public class AccountController {

  private final MemberService memberService;
  private final MessageService messageService;
  private final AccountRepository accountRepository;

  private static final String ACCOUNT_NOT_FOUND = "account.not_found";

  public AccountController(MemberService memberService,
                           MessageService messageService,
                           AccountRepository accountRepository) {
    this.memberService = memberService;
    this.messageService = messageService;
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
                                                 BindingResult bindingResult) {

    log.debug("updateAccountInfo: {}", updateAccountInfo);

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long memberId = memberService.currentMemberId();
    return accountRepository.findById(memberId)
       .map(account -> {
         account.setMemberId(memberId);
         return saveOrUpdate(updateAccountInfo, account);
       })
       .orElseGet(() -> saveOrUpdate(updateAccountInfo, new Account(memberId)));
  }

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

  @Data
  @NoArgsConstructor
  public static class UpdateAccountInfo {

    @NotNull @Size(max = 50)
    private String email;

    @NotNull @Size(max = 50)
    private String bankName;

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
    private String bankAccount;
    private String bankDepositor;
    private Boolean validity;
    private Date createdAt;
  }
}
