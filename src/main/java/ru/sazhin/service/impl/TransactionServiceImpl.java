package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.service.TransactionService;
import ru.sazhin.utils.Connection;
import ru.sazhin.utils.Preconditions;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionServiceImpl implements TransactionService {

    private Dao<Account, Long> accountDao;

    public TransactionServiceImpl() {
        try {
            accountDao =
                    DaoManager.createDao(Connection.INSTANCE.getConnection(), Account.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/from/{from}/to/{to}/amount/{amount}")
    @Override
    public Transaction transact(
            @PathParam("from") long fromId,
            @PathParam("to") long toId,
            @PathParam("amount") long amount) {
        Account from = null;
        Account to = null;
        try {
            from = accountDao.queryForId(fromId);
            to = accountDao.queryForId(toId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BigDecimal amnt = BigDecimal.valueOf(amount);

        Preconditions.checkNotNull(from, "Source account is null");
        Preconditions.checkNotNull(to, "Target account is null");
        Preconditions.checkNotNull(amount, "Amount account is null");
        Preconditions.checkNotNegative(amnt, "Amount should be not negative");

        Preconditions.checkNotNegative(from.getAmount().subtract(amnt),
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
                Preconditions.checkNotNegative(from.getAmount().subtract(amnt),
                        "Source amount should be not negative after transaction");

                from.changeAmount(amnt.multiply(BigDecimal.valueOf(-1)));
                to.changeAmount(amnt);
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }

        try {
            accountDao.update(from);
            accountDao.update(to);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Transaction(from, to, amnt);
    }
}
