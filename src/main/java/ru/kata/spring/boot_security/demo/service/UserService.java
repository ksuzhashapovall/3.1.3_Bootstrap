package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {
    void add(User user);
    void update(User user);
    void delete(Long id);
    User getById(Long id);
    User findByUsername(String username);
    List<User> getAll();
}