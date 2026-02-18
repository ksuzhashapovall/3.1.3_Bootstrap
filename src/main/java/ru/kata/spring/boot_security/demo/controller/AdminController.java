package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String adminPanel(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByUsername(username);

        if (currentUser == null) {
            currentUser = userService.getAll().stream().findFirst().orElse(null);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("newUser", new User());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("userToEdit", new User());
        model.addAttribute("allRoles", roleService.getAll());
        return "admin";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("newUser", new User());
        model.addAttribute("allRoles", roleService.getAll());
        return "add-new-user";
    }

    @PostMapping("/add")
    public String addUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam int age,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(required = false) String[] roles) {

        User user = new User(firstName, lastName, age, email, passwordEncoder.encode(password));

        Set<Role> roleSet = new HashSet<>();
        if (roles != null) {
            for (String roleName : roles) {
                roleSet.add(roleService.findByName(roleName));
            }
        } else {
            roleSet.add(roleService.findByName("ROLE_USER"));
        }
        user.setRoles(roleSet);

        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        model.addAttribute("allRoles", roleService.getAll());
        return "edit-user";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam int age,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) String[] roles) {

        User user = userService.getById(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAge(age);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        Set<Role> roleSet = new HashSet<>();
        if (roles != null) {
            for (String roleName : roles) {
                roleSet.add(roleService.findByName(roleName));
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