package kroryi.apikeymanager.service;

import kroryi.apikeymanager.entity.ApiKeyCallbackUrl;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.ApiKeyCallbackUrlRepository;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyCallbackUrlRepository callbackUrlRepository;

    public ApiKeyService(ApiKeyRepository apiKeyRepository, ApiKeyCallbackUrlRepository callbackUrlRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.callbackUrlRepository = callbackUrlRepository;
    }

    public boolean isValidKey(String apiKey, String clientIp) {
        Optional<ApiKeyEntity> apiKeyOpt = apiKeyRepository.findByKeyAndActiveIsTrue(apiKey);

        if (apiKeyOpt.isEmpty()) return false;

        ApiKeyEntity keyEntity = apiKeyOpt.get();

        // 만료 확인
        if (keyEntity.getExpiresAt() != null && keyEntity.getExpiresAt().isBefore(LocalDateTime.now()))
            return false;

        // IP 제한 확인
        if (keyEntity.getAllowedIp() != null && !keyEntity.getAllowedIp().equals(clientIp))
            return false;

        // 콜백 URL 확인
        if (callbackUrl != null) {
            List<ApiKeyCallbackUrl> allowed = callbackUrlRepository.findByApiKey_Key(apiKey);
            boolean match = allowed.stream().anyMatch(cb -> callbackUrl.startsWith(cb.getUrl()));
            if (!match) return false;
        }

        return true;
    }
}
