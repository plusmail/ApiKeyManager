package kroryi.apikeymanager.dto;

import lombok.*;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateApiKeyForm {
    private String name;
    private String allowedIp;
    private LocalDateTime expiresAt;
    private String callbackUrls; // 콤마로 구분된 문자열
}
