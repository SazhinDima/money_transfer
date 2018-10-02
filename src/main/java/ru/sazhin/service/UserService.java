package ru.sazhin.service;

import ru.sazhin.model.User;

import java.util.List;

public interface UserService {

    User get(long id);

    List<User> getAll();

    User create(User user);

    void delete(long id);

}
