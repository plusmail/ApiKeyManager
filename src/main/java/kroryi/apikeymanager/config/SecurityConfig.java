package kroryi.apikeymanager.config;

import kroryi.apikeymanager.service.ApiKeyService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public ApiKeyFilter apiKeyFilter(ApiKeyService apiKeyService) {
        return new ApiKeyFilter(apiKeyService);
    }
    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1); // 인증 필터 순서 조정 가능
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)// 실무에서는 disable하면 안됨.
//                .cors(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        // Swagger 경로 허용
//                        .requestMatchers(
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/swagger-ui.html"
//                        ).permitAll()
//
//                        // 그 외는 인증 필요
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults()); // 또는 formLogin(), 필요 시 제거 가능
//
//        return http.build();
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf().disable();
        return http.build();
    }
}
