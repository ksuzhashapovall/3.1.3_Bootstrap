package ru.kata.spring.boot_security.demo.configs;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.out.println("=== AUTHENTICATION SUCCESS ===");
        System.out.println("Username: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        System.out.println("Roles: " + roles);

        if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
            System.out.println("Redirecting to /admin");
            response.sendRedirect("/admin");
        } else if (roles.contains("ROLE_USER") || roles.contains("USER")) {
            System.out.println("Redirecting to /user");
            response.sendRedirect("/user");
        } else {
            System.out.println("Redirecting to /");
            response.sendRedirect("/");
        }
    }
}