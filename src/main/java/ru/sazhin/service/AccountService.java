package ru.sazhin.service;

import ru.sazhin.model.Account;
import ru.sazhin.model.User;

import java.util.List;

public interface AccountService {

    Account get(long id);

    List<Account> getAll();

    Account create(Account account, long userId);

    void delete(long id);

    /**
     * Get user related with account.
     */
    User getUser(long id);

}
