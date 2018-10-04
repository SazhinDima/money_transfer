package ru.sazhin.service.impl;

import com.j256.ormlite.dao.Dao;
import ru.sazhin.Database;
import ru.sazhin.model.User;
import ru.sazhin.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserServiceImpl implements UserService {

    private Dao<User, Long> userDao = Database.getInstance().getDao(User.class);

    @GET
    @Path("/{id}")
    @Override
    public User get(@PathParam("id") long id) {
        try {
            return userDao.queryForId(id);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Path("/all")
    @Override
    public List<User> getAll() {
        try {
            return userDao.queryForAll();
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

    @POST
    @Path("/create")
    @Override
    public User create(User user) {
        try {
            userDao.create(user);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
        return user;
    }

    @DELETE
    @Path("/{id}")
    @Override
    public void delete(@PathParam("id") long id)  {
        try {
            userDao.deleteById(id);
        } catch (SQLException e) {
            throw new WebApplicationException(e);
        }
    }

}
