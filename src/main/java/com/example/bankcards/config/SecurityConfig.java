package com.example.bankcards.config;

import com.example.bankcards.security.JwtTokenFilter;
import com.example.bankcards.security.SecurityExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/cards/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
