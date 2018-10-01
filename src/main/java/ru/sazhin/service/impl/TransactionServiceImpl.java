package ru.sazhin.service.impl;

import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.service.TransactionService;
import ru.sazhin.utils.Preconditions;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

public class TransactionServiceImpl implements TransactionService {


    @Override
    public Transaction transact(Account from, Account to, BigDecimal amount) {
        Preconditions.checkNotNull(from, "Source account is null");
        Preconditions.checkNotNull(to, "Target account is null");
        Preconditions.checkNotNull(amount, "Amount account is null");
        Preconditions.checkNotNegative(amount, "Amount should be not negative");

        Preconditions.checkNotNegative(from.getAmount().subtract(amount),
                "Source amount should be not negative after transaction");

        Lock lock1;
        Lock lock2;
        if (from.getId() > to.getId()) {
            lock1 = from.getWriteLock();
            lock2 = to.getWriteLock();
        } else {
            lock2 = from.getWriteLock();
            lock1 = to.getWriteLock();
        }

        lock1.lock();
        try {
            lock2.lock();
            try {
                Preconditions.checkNotNegative(from.getAmount().subtract(amount),
                        "Source amount should be not negative after transaction");

                from.changeAmount(amount.multiply(BigDecimal.valueOf(-1)));
                to.changeAmount(amount);
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }

        return new Transaction(1L, from, to, amount);
    }
}
