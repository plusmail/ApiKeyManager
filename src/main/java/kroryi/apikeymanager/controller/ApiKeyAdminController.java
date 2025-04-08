package kroryi.apikeymanager.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import kroryi.apikeymanager.entity.ApiKeyCallbackUrl;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.ApiKeyCallbackUrlRepository;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import kroryi.apikeymanager.service.ApiKeyService;
import kroryi.apikeymanager.utils.JwtTokenUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import kroryi.apikeymanager.utils.JwtTokenUtil;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin/api-keys")
@Log4j2
public class ApiKeyAdminController {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyCallbackUrlRepository callbackUrlRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public ApiKeyAdminController(ApiKeyRepository apiKeyRepository,
                                 ApiKeyCallbackUrlRepository callbackUrlRepository
            , JwtTokenUtil jwtTokenUtil) {
        this.apiKeyRepository = apiKeyRepository;
        this.callbackUrlRepository = callbackUrlRepository;
        this.jwtTokenUtil = jwtTokenUtil;

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
        ApiKeyEntity key = ApiKeyEntity.builder()
                .name(request.name)
                .active(true)
                .issuedAt(LocalDateTime.now())
                .expiresAt(request.expiresAt())
                .allowedIp(request.allowedIp())
                .build();

        // 콜백 URL 리스트 처리
        if (request.callbackUrls != null && !request.callbackUrls.isEmpty()) {
            for (String url : request.callbackUrls) {
                ApiKeyCallbackUrl cb = ApiKeyCallbackUrl.builder()
                        .url(url)
                        .apiKey(key)
                        .build();
                key.getCallbackUrls().add(cb); // 연관관계 설정
            }
        }

        ApiKeyEntity saved = apiKeyRepository.save(key);
        String jwt = jwtTokenUtil.generateToken(saved);

        saved.setKey(jwt); // JWT를 key로 저장
        return apiKeyRepository.save(saved);
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
    public ResponseEntity<?> getCallbackUrls(@PathVariable Long id,
                                             @RequestHeader("Authorization") String authHeader,
                                             @RequestHeader("callbackUrl") String callbackUrl) {
        try {
            String token = authHeader.replace("Bearer ", "");

            log.info("token: --->{}", token);

            Claims claims = jwtTokenUtil.parseToken(token);

            String keyIdFromToken = claims.getSubject();

            log.info("keyIdFromToken: --->{}", keyIdFromToken);
            if (!keyIdFromToken.equals(String.valueOf(id))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: ID mismatch");
            }

            List<ApiKeyCallbackUrl> urls = callbackUrlRepository.findByApiKey_Key(String.valueOf(id));
            return ResponseEntity.ok(urls);

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
    // 요청용 DTO
    public record CreateApiKeyRequest(
            String name,
            String allowedIp,
            LocalDateTime expiresAt,
            List<String> callbackUrls // 👈 여기에 추가

    ) {
    }
}