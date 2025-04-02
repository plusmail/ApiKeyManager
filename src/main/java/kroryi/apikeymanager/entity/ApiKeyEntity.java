package kroryi.apikeymanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "api_keys")
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "api_key")
    private String key;

    private String name;
    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
    private String allowedIp; // ← 이 필드를 통해 getAllowedIp() 가능

    @Builder.Default
    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiKeyCallbackUrl> callbackUrls = new ArrayList<>();

    // 기타 getter/setter
}