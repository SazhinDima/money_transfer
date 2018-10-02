package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import ru.sazhin.model.Account;
import ru.sazhin.model.User;
import ru.sazhin.service.AccountService;
import ru.sazhin.service.UserService;
import ru.sazhin.utils.Connection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountServiceImpl implements AccountService {

    private Dao<Account, Long> accountDao;

    public AccountServiceImpl() {
        try {
            accountDao =
                    DaoManager.createDao(Connection.INSTANCE.getConnection(), Account.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/{id}")
    @Override
    public Account get(@PathParam("id") long id) {
        try {
            return accountDao.queryForId(id);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("/getUser/{id}")
    @Override
    public User getUser(@PathParam("id") long id) {
        try {
            return accountDao.queryForId(id).getUser();
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("/all")
    @Override
    public List<Account> getAll() {
        try {
            return accountDao.queryForAll();
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("/create?userId={userId}")
    @Override
    public Account create(Account account,@PathParam("userId") long userId) {
        try {
            accountDao.create(account);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
        return account;
    }

    @DELETE
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") long id)  {
        try {
            accountDao.deleteById(id);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

}
