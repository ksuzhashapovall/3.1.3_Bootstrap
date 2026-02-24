package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public AdminController(UserService userService, RoleService roleService,
                           PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @GetMapping
    public String adminPanel(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);
        model.addAttribute("newUser", new UserDto());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("allRoles", roleService.getAll());
        return "admin";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("newUser") UserDto userDto,
                          @RequestParam(required = false) Long[] roleIds) {

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Set<Role> roleSet = new HashSet<>();
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                roleService.getById(roleId).ifPresent(roleSet::add);
            }
        } else {
            Role userRole = roleService.findByName("ROLE_USER");
            if (userRole != null) {
                roleSet.add(userRole);
            }
        }
        userDto.setRoles(roleSet);

        User user = userMapper.toEntity(userDto);
        userService.add(user);

        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAll());
        return "edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam int age,
                             @RequestParam String email,
                             @RequestParam(required = false) String newPassword,
                             @RequestParam(required = false) Long[] roleIds) {

        User user = userService.getById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);
        user.setUsername(email); // username всегда равен email

        // Обновляем пароль только если введен новый
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        Set<Role> roleSet = new HashSet<>();
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                roleService.getById(roleId).ifPresent(roleSet::add);
            }
        }
        user.setRoles(roleSet);

        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}