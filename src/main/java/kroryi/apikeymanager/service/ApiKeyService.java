package kroryi.apikeymanager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import kroryi.apikeymanager.entity.ApiKeyCallbackUrl;
import kroryi.apikeymanager.entity.ApiKeyEntity;
import kroryi.apikeymanager.repository.ApiKeyCallbackUrlRepository;
import kroryi.apikeymanager.repository.ApiKeyRepository;
import kroryi.apikeymanager.utils.JwtTokenUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyCallbackUrlRepository callbackUrlRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public ApiKeyService(ApiKeyRepository apiKeyRepository,
                         ApiKeyCallbackUrlRepository callbackUrlRepository,
                         JwtTokenUtil jwtTokenUtil) {
        this.apiKeyRepository = apiKeyRepository;
        this.callbackUrlRepository = callbackUrlRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public boolean isValidKey(String jwtToken, String clientIp, String callbackUrl) {
        try {
            Claims claims = jwtTokenUtil.parseToken(jwtToken);
            String keyId = claims.getSubject();

            Optional<ApiKeyEntity> keyOpt = apiKeyRepository.findById(Long.valueOf(keyId));
            if (keyOpt.isEmpty() || !keyOpt.get().getActive()) return false;

            ApiKeyEntity key = keyOpt.get();

            // IP 제한
            if (key.getAllowedIp() != null && !key.getAllowedIp().equals(clientIp)) return false;

            // ✅ 콜백 URL 제한 검사
            if (callbackUrl != null) {
                List<ApiKeyCallbackUrl> allowed =
                        callbackUrlRepository.findByApiKey_Key(key.getKey()); // JWT를 key로 쓰는 경우

                boolean match = allowed.stream().anyMatch(cb -> callbackUrl.startsWith(cb.getUrl()));
                if (!match) return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
