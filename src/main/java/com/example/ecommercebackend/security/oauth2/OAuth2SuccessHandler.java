package com.example.ecommercebackend.security.oauth2;

import com.example.ecommercebackend.entity.Role;
import com.example.ecommercebackend.entity.User;
import com.example.ecommercebackend.repository.RoleRepository;
import com.example.ecommercebackend.repository.UserRepository;
import com.example.ecommercebackend.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            String givenName = (String) attributes.get("given_name");
            String familyName = (String) attributes.get("family_name");
            newUser.setFirstName(givenName != null ? givenName : "");
            newUser.setLastName(familyName != null ? familyName : "");
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

            Role role = roleRepository.findByName("ROLE_USER");
            newUser.setRoles(Set.of(role));

            return userRepository.save(newUser);
        });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String accessToken = jwtUtils.generateOAuthAccessToken(user.getEmail(), user.getId(), roles);
        String refreshToken = jwtUtils.generateOAuthRefreshToken(user.getEmail(), user.getId(), roles);

        String tokenId = jwtUtils.extractTokenId(refreshToken);
        redisTemplate.opsForValue().set(
                "refresh:" + tokenId,
                refreshToken,
                30,
                TimeUnit.DAYS
        );

        String code = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "oauth2:code:" + code,
                accessToken,
                30,
                TimeUnit.SECONDS
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String targetUrl = "http://localhost:5173/login-success?code=" + code;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
