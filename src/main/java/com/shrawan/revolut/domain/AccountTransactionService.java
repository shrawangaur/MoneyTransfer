package com.shrawan.revolut.domain;

import com.shrawan.revolut.domain.model.Account;

import java.util.function.BiConsumer;

public interface AccountTransactionService {

    void doInTransaction(Account account1, Account account2, BiConsumer<Account, Account> action);
}
