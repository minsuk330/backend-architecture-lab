package com.backend.lab.common.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.JwtProperties;
import com.backend.lab.common.auth.jwt.filter.JwtFilter;
import com.backend.lab.common.auth.session.CustomExpiredSessionStrategy;
import com.backend.lab.common.auth.session.HeaderSessionFilter;
import com.backend.lab.common.auth.session.repository.CustomSessionRepository;
import com.backend.lab.common.exception.CustomAccessDeniedHandler;
import com.backend.lab.common.exception.CustomAuthEntryPoint;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ObjectMapper objectMapper;

  private final JwtProperties jwtProperties;
  private final MemberService memberService;
  private final AdminService adminService;

  @Bean
  public Jwt jwt() {
    return new Jwt(
        jwtProperties.getClientSecret(),
        jwtProperties.getIssuer(),
        jwtProperties.getTokenExpire(),
        jwtProperties.getRefreshTokenExpire()
    );
  }

  public JwtFilter jwtFilter() {
    return new JwtFilter(jwt(), memberService, adminService);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of(
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:5173",
            "http://localhost:8080",
            "https://api.kmorgan.co.kr",
            "https://admin.kmorgan.co.kr",
            "https://www.kmorgan.co.kr",
            "https://kmorgan.co.kr",
            "https://kmorgan-admin.vercel.app"
        )
    );
    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "PUT", "PATCH", "DELETE"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    configuration.setAllowedHeaders(
        List.of(
            "Origin", "Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method",
            "Access-Control-Request-Headers", "Authorization", "access_token", "refresh_token",
            "Access-Control-Allow-Origin", "X-KMORGAN-SID"
        ));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public CustomAuthEntryPoint authenticationEntryPoint() {
    return new CustomAuthEntryPoint(objectMapper);
  }

  @Bean
  public CustomAccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler(objectMapper);
  }

  @Bean
  public SecurityFilterChain memberChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/member/**", "/seller/**", "/agent/**", "/buyer/**", "/common/**", "/nonMember/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/**").permitAll()
            .anyRequest().authenticated()
        )
        .headers(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .rememberMe(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
        )
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
        .cors(withDefaults());

    return http.build();
  }

  @Bean
  public CustomSessionRepository customSessionRepository() {
    return new CustomSessionRepository();
  }

  public HeaderSessionFilter headerSessionFilter() {
    return new HeaderSessionFilter(customSessionRepository());
  }

  @Bean
  public SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
    http
        .securityMatcher("/admin/**")
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/**").permitAll()
            .anyRequest().authenticated()
        )
        .headers(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .rememberMe(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(authenticationEntryPoint())
            .accessDeniedHandler(accessDeniedHandler())
        )
        .addFilterBefore(headerSessionFilter(), UsernamePasswordAuthenticationFilter.class)
        .cors(withDefaults());

    return http.build();
  }

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
    return new CustomExpiredSessionStrategy(objectMapper);
  }
}
