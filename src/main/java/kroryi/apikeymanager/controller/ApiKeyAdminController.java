package kroryi.apikeymanager.controller;

import jakarta.transaction.Transactional;
import kroryi.apikeymanager.entity.ApiKeyCallbackUrl;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.ApiKeyCallbackUrlRepository;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/api-keys")
public class ApiKeyAdminController {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyCallbackUrlRepository callbackUrlRepository;

    public ApiKeyAdminController(ApiKeyRepository apiKeyRepository, ApiKeyCallbackUrlRepository callbackUrlRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.callbackUrlRepository = callbackUrlRepository;
    }

    /**
     * API Key 전체 목록 조회
     */
    @GetMapping
    public List<ApiKeyEntity> getAll() {
        return apiKeyRepository.findAll();
    }

    /**
     * API Key 생성
     */
    @PostMapping
    public ApiKeyEntity createKey(@RequestBody CreateApiKeyRequest request) {
        ApiKeyEntity key = new ApiKeyEntity();
        key.setKey(UUID.randomUUID().toString().replace("-", ""));
        key.setName(request.name());
        key.setActive(true);
        key.setIssuedAt(LocalDateTime.now());
        key.setExpiresAt(request.expiresAt());
        key.setAllowedIp(request.allowedIp());

        return apiKeyRepository.save(key);
    }

    /**
     * API Key 활성/비활성 변경
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        Optional<ApiKeyEntity> opt = apiKeyRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        ApiKeyEntity key = opt.get();
        key.setActive(active);
        apiKeyRepository.save(key);
        return ResponseEntity.ok().build();
    }

    /**
     * API Key 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKey(@PathVariable Long id) {
        if (!apiKeyRepository.existsById(id)) return ResponseEntity.notFound().build();
        apiKeyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 콜백 URL 추가
     */
    @PostMapping("/{id}/callback-urls")
    @Transactional
    public ResponseEntity<?> addCallbackUrl(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String url = body.get("url");
        ApiKeyEntity key = apiKeyRepository.findById(id).orElse(null);
        if (key == null) return ResponseEntity.notFound().build();

        ApiKeyCallbackUrl cb = new ApiKeyCallbackUrl();
        cb.setUrl(url);
        cb.setApiKey(key);
        callbackUrlRepository.save(cb);

        return ResponseEntity.ok().build();
    }

    /**
     * 콜백 URL 삭제
     */
    @DeleteMapping("/callback-urls/{callbackId}")
    public ResponseEntity<?> deleteCallbackUrl(@PathVariable Long callbackId) {
        if (!callbackUrlRepository.existsById(callbackId)) return ResponseEntity.notFound().build();
        callbackUrlRepository.deleteById(callbackId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 특정 Key의 콜백 URL 목록
     */
    @GetMapping("/{id}/callback-urls")
    public List<ApiKeyCallbackUrl> getCallbackUrls(@PathVariable Long id) {
        return callbackUrlRepository.findByApiKey_Key(String.valueOf(id));
    }

    // 요청용 DTO
    public record CreateApiKeyRequest(
            String name,
            String allowedIp,
            LocalDateTime expiresAt
    ) {
    }
}