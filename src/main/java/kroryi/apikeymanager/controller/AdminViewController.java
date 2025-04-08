package kroryi.apikeymanager.controller;

import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import kroryi.apikeymanager.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AdminViewController {

    private final CustomUserDetailsService userDetailsService;
    private final ApiKeyRepository apiKeyRepository;


    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html 로 연결됨
    }

    @GetMapping
    public String dashboard(Model model) {
        List<ApiKeyEntity> recent = apiKeyRepository.findAll(Sort.by(Sort.Direction.DESC, "issuedAt"))
                .stream()
                .limit(5)
                .toList();
        model.addAttribute("recentKeys", recent);
        return "dashboard";
    }

}


