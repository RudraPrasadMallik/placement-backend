package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        },
        allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse,
                                   HttpSession session) {
        LoginResponse response = authService.login(loginRequest);

        if (response.isSuccess()) {
            ResponseCookie authCookie = ResponseCookie.from(com.example.demo.config.JwtAuthenticationFilter.AUTH_TOKEN_COOKIE, response.getToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(response.getExpiresAt() > 0
                            ? Math.max(1, (response.getExpiresAt() - System.currentTimeMillis()) / 1000)
                            : -1)
                    .build();
            httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());
            session.setAttribute("role", response.getRole());
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("email", response.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        ResponseCookie clearCookie = ResponseCookie.from(com.example.demo.config.JwtAuthenticationFilter.AUTH_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("message", "Logged out successfully.");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> session(Authentication authentication, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("role", user.getRole().name());
        session.setAttribute("userId", user.getId());
        session.setAttribute("email", user.getEmail());

        response.put("authenticated", true);
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole().name());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "Auth API is working!";
    }
}
