package kroryi.apikeymanager.repository;

import kroryi.apikeymanager.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

    /**
     * 활성화된 API Key 조회 (기본 인증용)
     */
    Optional<ApiKeyEntity> findByKeyAndActiveIsTrue(String key);

    /**
     * 전체 키 조회 (관리자 조회용)
     */
    Optional<ApiKeyEntity> findByKey(String key);

    /**
     * 존재 여부만 빠르게 확인 (성능 최적화용)
     */
    boolean existsByKey(String key);

    // 필요 시 사용자 정의 쿼리 추가 가능 (예: @Query 사용 등)
}