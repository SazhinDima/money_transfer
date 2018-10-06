package ru.sazhin.service;

import ru.sazhin.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    Transaction get(long id);

    List<Transaction> getAll();

    /**
     * Create transaction.
     * @param fromId - Account id (Source).
     * @param toId - Account id (Target).
     * @param amount - Amount.
     * @return Transaction.
     */
    Transaction transact(long fromId, long toId, BigDecimal amount);
}
