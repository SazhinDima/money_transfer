package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import ru.sazhin.Database;
import ru.sazhin.model.Account;
import ru.sazhin.model.Transaction;
import ru.sazhin.service.TransactionService;
import ru.sazhin.utils.Preconditions;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionServiceImpl implements TransactionService {

    private Dao<Account, Long> accountDao = Database.getInstance().getDao(Account.class);
    private Dao<Transaction, Long> transactionDao = Database.getInstance().getDao(Transaction.class);

    @GET
    @Path("/{id}")
    @Override
    public Transaction get(@PathParam("id") long id) {
        try {
            return transactionDao.queryForId(id);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("/all")
    @Override
    public List<Transaction> getAll() {
        try {
            return transactionDao.queryForAll();
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("/from/{from}/to/{to}/amount/{amount}")
    @Override
    public Transaction transact(
            @PathParam("from") long fromId,
            @PathParam("to") long toId,
            @PathParam("amount") BigDecimal amount) {
        try {
            return TransactionManager.callInTransaction(Database.getInstance().getConnection(), () -> {
                        String[] row = accountDao
                                .queryRaw("select amount from Account where id = ? for update",
                                        Long.toString(fromId))
                                .getFirstResult();
                        accountDao
                                .queryRaw("select amount from Account where id = ? for update",
                                        Long.toString(toId));

                        Preconditions.checkNotNull(row, "Account not found");

                        BigDecimal from = new BigDecimal(row[0]);

                        Preconditions.checkNotNegative(from.subtract(amount),
                                "Amount should be not negative");

                        accountDao.executeRawNoArgs("update Account set amount = amount - "
                                + amount.toString() + " where id = " + Long.toString(fromId));
                        accountDao.executeRawNoArgs("update Account set amount = amount + "
                                + amount.toString() + " where id = " + Long.toString(toId));

                        Transaction transaction = new Transaction(
                                accountDao.queryForId(fromId),
                                accountDao.queryForId(toId), amount);
                        transactionDao.create(transaction);

                        return transaction;
                    }
            );
        } catch (SQLException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

    }

}
