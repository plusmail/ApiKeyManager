package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.dto.RegisterRequest;
import kroryi.apikeymanager.entity.AdminUser;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.AdminUserRepository;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyRepository apiKeyRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AdminUser());
        return "register";
    }


    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") AdminUser user, Model model) {
        if (adminUserRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "이미 존재하는 사용자입니다.");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        user.setRegisteredAt(LocalDateTime.now());
        AdminUser savedUser = adminUserRepository.save(user);

        // 🔐 API Key 자동 생성 및 저장
        String apiKey = UUID.randomUUID().toString();
        ApiKeyEntity apiKeyEntity = ApiKeyEntity.builder()
                .key(apiKey)
                .name(savedUser.getUsername())
                .issuedAt(LocalDateTime.now())
                .build();
        apiKeyRepository.save(apiKeyEntity);

        model.addAttribute("apiKey", apiKey);
        return "register-success"; // API 키 보여주는 페이지
    }
}
