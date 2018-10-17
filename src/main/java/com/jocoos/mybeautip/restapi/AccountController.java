package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.account.Account;
import com.jocoos.mybeautip.member.account.AccountRepository;

@Slf4j
@RestController
@RequestMapping("/api/1/members/me/account")
public class AccountController {

  private final MemberService memberService;
  private final AccountRepository accountRepository;

  public AccountController(MemberService memberService,
                           AccountRepository accountRepository) {
    this.memberService = memberService;
    this.accountRepository = accountRepository;
  }

  @GetMapping
  public ResponseEntity<AccountInfo> getAccount() {
    Long memberId = memberService.currentMemberId();
    return accountRepository.findById(memberId)
       .map(account -> {
         log.debug("account: {}", account);
         AccountInfo accountInfo = new AccountInfo();
         BeanUtils.copyProperties(account, accountInfo);
         log.debug("accountInfo: {}", accountInfo);

         return new ResponseEntity<>(accountInfo, HttpStatus.OK);
       }).orElseThrow(() -> new NotFoundException("not_found_account", "Account not found"));
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

    return new ResponseEntity<AccountInfo>(accountInfo, HttpStatus.OK);
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
