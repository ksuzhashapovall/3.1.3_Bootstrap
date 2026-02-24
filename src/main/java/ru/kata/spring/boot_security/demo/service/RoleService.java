package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAll();
    Role findByName(String name);
    Optional<Role> getById(Long id); // Новый метод
    void add(Role role);
    void update(Role role);
    void delete(Long id);
}