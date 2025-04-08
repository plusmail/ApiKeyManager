package kroryi.apikeymanager.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String allowedIp;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "apiKey", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 👈 추가
    private List<ApiKeyCallbackUrl> callbackUrls = new ArrayList<>();

    // equals, hashCode, toString은 필요 시 직접 작성하거나 lombok으로 제한적으로 생성
}
