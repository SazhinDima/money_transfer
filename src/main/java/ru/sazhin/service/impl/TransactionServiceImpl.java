package ru.sazhin.service.impl;

import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.service.TransactionService;
import ru.sazhin.utils.Preconditions;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

public class TransactionServiceImpl implements TransactionService {


    @Override
    public Transaction transact(Account from, Account to, BigDecimal ammount) {
        Preconditions.checkNotNull(from, "Source account is null");
        Preconditions.checkNotNull(to, "Target account is null");
        Preconditions.checkNotNull(ammount, "Ammount account is null");
        Preconditions.checkNotNegative(ammount, "Ammount should be not negative");

        Preconditions.checkNotNegative(from.getAmount().subtract(ammount),
                "Source ammount should be not negative after transaction");

        Lock lock1;
        Lock lock2;
        if (from.getId() > to.getId()) {
            lock1 = from.getLock();
            lock2 = to.getLock();
        } else {
            lock2 = from.getLock();
            lock1 = to.getLock();
        }

        lock1.lock();
        try {
            lock2.lock();
            try {
                Preconditions.checkNotNegative(from.getAmount().subtract(ammount),
                        "Source ammount should be not negative after transaction");

                from.changeAmount(ammount.multiply(BigDecimal.valueOf(-1)));
                to.changeAmount(ammount);
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }

        return new Transaction(1L, from, to, ammount);
    }
}
