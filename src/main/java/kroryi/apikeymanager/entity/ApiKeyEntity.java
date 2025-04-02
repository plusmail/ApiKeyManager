package kroryi.apikeymanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "api_keys")
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String key;

    private String name;

    private Boolean active = true;

    private LocalDateTime issuedAt = LocalDateTime.now();

    private LocalDateTime expiresAt;
    private String allowedIp; // ← 이 필드를 통해 getAllowedIp() 가능

    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiKeyCallbackUrl> callbackUrls = new ArrayList<>();

    // 기타 getter/setter
}