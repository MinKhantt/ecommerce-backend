package com.example.ecommercebackend.security.config;

import com.example.ecommercebackend.security.jwt.AuthTokenFilter;
import com.example.ecommercebackend.security.jwt.JwtAuthEntryPoint;
import com.example.ecommercebackend.security.jwt.JwtUtils;
import com.example.ecommercebackend.security.oauth2.OAuth2SuccessHandler;
import com.example.ecommercebackend.security.user.ShopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class ShopConfig {

    private final ShopUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint authEntryPoint;
    private final JwtUtils jwtUtils;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final List<String> SECURE_URLS = List.of(
            "/api/v1/carts/**",
            "/api/v1/cartItems/**",
            "/api/v1/payments/**"
    );

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        var authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->exception.authenticationEntryPoint(authEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/payments/webhook").permitAll()
                        .requestMatchers(SECURE_URLS.toArray(String[]::new))
                        .authenticated()
                        .anyRequest()
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            String targetUrl = UriComponentsBuilder.fromUriString(normalizedFrontendUrl())
                                    .path("/login")
                                    .queryParam("error", exception.getMessage())
                                    .build()
                                    .toUriString();
                            response.sendRedirect(targetUrl);
                        })
                )
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .cors(
                        cors -> cors.configurationSource(request -> {
                            var corsConfig = new CorsConfiguration();
                            corsConfig.setAllowedOrigins(List.of(normalizedFrontendUrl()));
                            corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                            corsConfig.setAllowedHeaders(List.of("*"));
                            return corsConfig;
                        })
                );

        return httpSecurity.build();
    }

    private String normalizedFrontendUrl() {
        return frontendUrl.replaceAll("/+$", "");
    }

}
