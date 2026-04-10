package com.example.demo.controller;

import com.example.demo.config.JwtAuthenticationFilter;
import com.example.demo.config.JwtService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final JwtService jwtService;

    public PageController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session,
                            @CookieValue(name = JwtAuthenticationFilter.AUTH_TOKEN_COOKIE, required = false) String authToken) {
        String role = resolveRole(session, authToken);
        if ("STUDENT".equals(role)) {
            return "redirect:/student-dashboard";
        }
        if ("ADMIN".equals(role)) {
            return "redirect:/admin-dashboard";
        }
        return "forward:/login.html";
    }

    @GetMapping("/student-dashboard")
    public String studentDashboard(HttpSession session,
                                   @CookieValue(name = JwtAuthenticationFilter.AUTH_TOKEN_COOKIE, required = false) String authToken) {
        String role = resolveRole(session, authToken);
        if (!"STUDENT".equals(role)) {
            return "redirect:/login";
        }
        return "forward:/student-dashboard.html";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard(HttpSession session,
                                 @CookieValue(name = JwtAuthenticationFilter.AUTH_TOKEN_COOKIE, required = false) String authToken) {
        String role = resolveRole(session, authToken);
        if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        return "forward:/admin-dashboard.html";
    }

    private String resolveRole(HttpSession session, String authToken) {
        String role = (String) session.getAttribute("role");
        if (role != null && !role.isBlank()) {
            return role;
        }

        if (authToken == null || authToken.isBlank()) {
            return null;
        }

        try {
            String tokenRole = jwtService.extractRole(authToken);
            if (tokenRole != null && !tokenRole.isBlank()) {
                session.setAttribute("role", tokenRole);
            }
            Long userId = jwtService.extractUserId(authToken);
            if (userId != null) {
                session.setAttribute("userId", userId);
            }
            String email = jwtService.extractUsername(authToken);
            if (email != null && !email.isBlank()) {
                session.setAttribute("email", email);
            }
            return tokenRole;
        } catch (Exception ignored) {
            return null;
        }
    }
}
