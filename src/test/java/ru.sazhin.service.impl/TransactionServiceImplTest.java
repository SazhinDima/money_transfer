package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sazhin.Database;
import ru.sazhin.model.Account;
import ru.sazhin.model.User;
import ru.sazhin.service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransactionServiceImplTest {

    private TransactionService transactionService = new TransactionServiceImpl();
    private Dao<Account, Long> accountDao = Database.getInstance().getDao(Account.class);

    @Before
    public void up() {
        Database.getInstance().createTables();
    }

    @After
    public void down() {
        Database.getInstance().closeConnection();
    }

    @Test
    public void testTransact() throws SQLException {
        Account accountFrom = new Account(new User(), BigDecimal.TEN);
        accountDao.create(accountFrom);
        Account accountTo = new Account(new User(), BigDecimal.TEN);
        accountDao.create(accountTo);

        transactionService.transact(accountFrom.getId(), accountTo.getId(), BigDecimal.valueOf(5));

        Assert.assertEquals(BigDecimal.valueOf(5), accountDao.queryForId(accountFrom.getId()).getAmount());
        Assert.assertEquals(BigDecimal.valueOf(15), accountDao.queryForId(accountTo.getId()).getAmount());
    }

}
