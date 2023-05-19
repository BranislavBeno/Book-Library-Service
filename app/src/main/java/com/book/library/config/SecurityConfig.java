package com.book.library.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(BasicAuthProperties.class)
public class SecurityConfig {

    private static final String LOGIN_URL = "/login";

    private final BasicAuthProperties props;

    public SecurityConfig(BasicAuthProperties props) {
        this.props = props;
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
                .httpBasic(basic -> basic.authenticationEntryPoint(userAuthenticationEntryPoint()))
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
                        .requestMatchers(LOGIN_URL)
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form.loginPage(LOGIN_URL).defaultSuccessUrl("/"))
                .logout(logout -> logout.invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl(LOGIN_URL + "?logout"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true));

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(props.getUserDetails());
    }

    @Bean
    public AuthenticationEntryPoint userAuthenticationEntryPoint() {
        UserAuthenticationEntryPoint userAuthenticationEntryPoint = new UserAuthenticationEntryPoint();
        userAuthenticationEntryPoint.setRealmName("Basic Authentication");

        return userAuthenticationEntryPoint;
    }
}
