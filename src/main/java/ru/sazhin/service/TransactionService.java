package ru.sazhin.service;

import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction transact(long fromId, long toId, long amount);
}
