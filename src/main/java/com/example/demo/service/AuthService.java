package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.AppUser;
import com.example.demo.model.PasswordResetToken;
import com.example.demo.repo.PasswordResetTokenRepository;
import com.example.demo.repo.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder encoder;

    public AuthService(UserRepository userRepo, PasswordResetTokenRepository tokenRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
    }

    // RF1 - Registro
    public AppUser register(String email, String password) {
        if (userRepo.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // ✅ Guarda el role SIN "ROLE_" para que tu JwtAuthFilter funcione bien
        AppUser u = new AppUser(email, encoder.encode(password), "USER");

        return userRepo.save(u);
    }

    // RF2 - Login (validación)
    public AppUser validateLogin(String email, String password) {
        AppUser u = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // ✅ FALTABA ESTO
        return u;
    }

    // RF3 - Recuperación (token en consola)
    public String createResetToken(String email) {
        AppUser u = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken t = new PasswordResetToken(
                token,
                u,
                LocalDateTime.now().plusMinutes(15)
        );

        tokenRepo.save(t);

        System.out.println("PASSWORD RESET TOKEN for " + email + ": " + token);

        return token;
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken t = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        AppUser u = t.getUser();
        u.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(u);

        // opcional: borrar token para que no se reuse
        tokenRepo.delete(t);
    }
}