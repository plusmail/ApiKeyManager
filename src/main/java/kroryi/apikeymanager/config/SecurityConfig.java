package kroryi.apikeymanager.config;

import kroryi.apikeymanager.service.ApiKeyService;
import kroryi.apikeymanager.service.CustomUserDetailsService;
import kroryi.apikeymanager.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;


    @Bean
    public ApiKeyFilter apiKeyFilter(ApiKeyService apiKeyService, JwtTokenUtil jwtTokenUtil) {
        return new ApiKeyFilter(apiKeyService, jwtTokenUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/img/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/images/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/api/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/uploads/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/upload-form/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/multi-upload-form/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/uploadImage/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/admin/**").permitAll() // 이제는 인증 필요로 변경
                        .requestMatchers("/admin/api-keys").permitAll() // ✅ 임시로 발급 경로 허용
                        .requestMatchers("/error").permitAll() // ✅ 이 줄 추가!
                        .requestMatchers("/register", "/register-success").permitAll()
                        .requestMatchers(
                                "/js/**", "/css/**", "/images/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/index.html","/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs/swagger-config", "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // or disable()
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class); // ✅ 이게 핵심!!

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
