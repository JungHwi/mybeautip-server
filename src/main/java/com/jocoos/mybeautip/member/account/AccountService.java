package com.jocoos.mybeautip.member.account;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.restapi.AccountController;

@Slf4j
@Service
public class AccountService {
  private final AccountRepository accountRepository;
  
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }
  
  @Transactional
  public AccountController.AccountInfo saveOrUpdate(
      AccountController.UpdateAccountInfo updateAccountInfo, Account account) {
    if (updateAccountInfo != null) {
      BeanUtils.copyProperties(updateAccountInfo, account);
    }
    
    accountRepository.save(account);
    log.debug("account: {}", account);
    
    AccountController.AccountInfo accountInfo = new AccountController.AccountInfo();
    BeanUtils.copyProperties(account, accountInfo);
    log.debug("accountInfo: {}", accountInfo);
    
    return accountInfo;
  }
}
