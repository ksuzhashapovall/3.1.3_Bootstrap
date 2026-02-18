package ru.kata.spring.boot_security.demo.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void initData() {
        initRoles();
        initTestUsers();
    }

    @Transactional
    public void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_USER"));
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
    }

    @Transactional
    public void initTestUsers() {
        Role userRole = roleRepository.findByName("ROLE_USER");
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        // Создаем админа
        if (userRepository.findByEmail("admin@mail.ru") == null) {
            User admin = new User("admin", "admin", 35, "admin@mail.ru", passwordEncoder.encode("admin"));
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        } else {
            User admin = userRepository.findByEmail("admin@mail.ru");
            boolean needUpdate = false;

            if (!admin.getRoles().contains(adminRole)) {
                admin.getRoles().add(adminRole);
                needUpdate = true;
            }
            if (!admin.getRoles().contains(userRole)) {
                admin.getRoles().add(userRole);
                needUpdate = true;
            }

            if (needUpdate) {
                userRepository.save(admin);
            }
        }

        // Создаем обычного пользователя
        if (userRepository.findByEmail("user@mail.ru") == null) {
            User user = new User("user", "user", 30, "user@mail.ru", passwordEncoder.encode("user"));
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);
            userRepository.save(user);
        } else {
            User user = userRepository.findByEmail("user@mail.ru");
            if (!user.getRoles().contains(userRole)) {
                user.getRoles().add(userRole);
                userRepository.save(user);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole != null) {
            roles.add(userRole);
        }
        return roles;
    }
}