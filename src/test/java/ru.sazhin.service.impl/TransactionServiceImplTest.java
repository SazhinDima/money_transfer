package ru.sazhin.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.service.TransactionService;

import java.math.BigDecimal;

public class TransactionServiceImplTest {

    private TransactionService transactionService;

    @Before
    public void prepare() {
        transactionService = new TransactionServiceImpl();
    }

    @Test(expected = NullPointerException.class)
    public void testTransactFromIsNull() {
        transactionService.transact(null, new Account(0L, BigDecimal.ZERO), BigDecimal.TEN);
    }

    @Test(expected = NullPointerException.class)
    public void testTransactToIsNull() {
        transactionService.transact(new Account(0L, BigDecimal.ZERO), null, BigDecimal.TEN);
    }

    @Test(expected = NullPointerException.class)
    public void testTransactAmountIsNull() {
        transactionService.transact(
                new Account(0L, BigDecimal.ZERO), new Account(1L, BigDecimal.ZERO), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransactAmountIsNegative() {
        transactionService.transact(
                new Account(0L, BigDecimal.ZERO), new Account(1L, BigDecimal.ZERO), BigDecimal.valueOf(-1));
    }

    @Test
    public void testTransact1() {
        Account accountFrom = new Account(0L, BigDecimal.TEN);
        Account accountTo = new Account(1L, BigDecimal.TEN);
        transactionService.transact(accountFrom, accountTo, BigDecimal.valueOf(5));

        Assert.assertEquals(BigDecimal.valueOf(5), accountFrom.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(15), accountTo.getAmount());
    }

}
