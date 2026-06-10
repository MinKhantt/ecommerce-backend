package com.example.shoppingcartapi.security.oauth2;

import com.example.shoppingcartapi.entity.Role;
import com.example.shoppingcartapi.entity.User;
import com.example.shoppingcartapi.repository.RoleRepository;
import com.example.shoppingcartapi.repository.UserRepository;
import com.example.shoppingcartapi.security.jwt.JwtUtils;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, java.io.IOException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFirstName((String) attributes.get("given_name"));
            newUser.setLastName((String) attributes.get("family_name"));

            Role role = roleRepository.findByName("ROLE_USER");
            newUser.setRoles(Set.of(role));

            return userRepository.save(newUser);
        });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String jwt = jwtUtils.generateToken(user.getEmail(), user.getId(), roles);

        String targetUrl = "http://localhost:5173/login-success?token=" + jwt;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
