package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Getter
  private NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public String transferMoney(String accountFrom, String accountTo, double amount) {
         Optional<Account> fromAccount = Optional.ofNullable(this.getAccount(accountFrom));
        Optional<Account> toAccount = Optional.ofNullable(this.getAccount(accountTo));

    if (fromAccount.get().getBalance().doubleValue() < amount ) {
       return "Not sufficient amount in  "+accountFrom;
    } else {
      transfer(fromAccount, toAccount, BigDecimal.valueOf(amount));
    }

    return "success";
  }

  @Transactional
  public void transfer(Optional<Account> fromAccount, Optional<Account> toAccount, BigDecimal amount) {
    fromAccount.get().setBalance(fromAccount.get().getBalance().subtract(amount));
    toAccount.get().setBalance(toAccount.get().getBalance().add(amount));
    notificationService.notifyAboutTransfer(fromAccount.get(), amount.toString() +" amount debit fraom you account");
    notificationService.notifyAboutTransfer(toAccount.get(), amount.toString() +" amount credit fraom you account");
  }
}
