package com.book.library.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";

    private final LogoutSuccessHandler logoutSuccessHandler;

    @Value("${spring.security.oauth2.client.registration.cognito.clientId}")
    private String resourceId;

    @Value("${jwt.auth.converter.principalAttribute:username}")
    private String userNameAttribute;

    public SecurityConfig(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain restFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/api/v1/books/**")
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/api/v1/books/**").authenticated())
                .oauth2Login(oauth2 -> new OAuth2LoginConfigurer<>())
                .exceptionHandling(exception -> exception.accessDeniedHandler(new UserForbiddenErrorHandler()));

        return httpSecurity.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                        .permitAll()
                        .requestMatchers(LOGIN_URL, "/actuator/info", "/register", "/change-password")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2Login(oauth2 -> oauth2.loginPage(LOGIN_URL).defaultSuccessUrl("/books?page=0", true))
                .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1));

        return httpSecurity.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService(@Autowired JwtDecoder jwtDecoder) {
        return userRequest -> {
            Jwt jwt = jwtDecoder.decode(userRequest.getAccessToken().getTokenValue());
            Collection<? extends GrantedAuthority> authorities = extractRoles(jwt);

            OidcUserService delegate = new OidcUserService();
            OidcUser oidcUser = delegate.loadUser(userRequest);

            String nameAttributeKey = userNameAttribute == null ? JwtClaimNames.SUB : userNameAttribute;

            return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
        };
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);
        if (resource == null) {
            return Collections.emptySet();
        }

        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
        if (resourceRoles == null) {
            return Collections.emptySet();
        }

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
