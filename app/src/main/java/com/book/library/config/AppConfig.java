package com.book.library.config;

import com.book.library.dto.BookMapper;
import com.book.library.repository.BookFileRepository;
import com.book.library.repository.BookRepository;
import com.book.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppConfig {

    @Bean
    public BookRepository bookRepository(@Value("${book.repository.path}") String path) {
        return new BookFileRepository(path);
    }

    @Bean
    public BookService bookService(@Autowired BookRepository repository,
                                   @Value("${book.service.page.size:20}") int pageSize) {
        return new BookService(repository, pageSize);
    }

    @Bean
    public BookMapper bookMapper() {
        return new BookMapper();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                        .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .csrf().disable();

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService(@Value("${book.authentication.user}") String userName,
                                                 @Value("${book.authentication.password}") String password) {

        UserDetails user = User.withDefaultPasswordEncoder()
                .username(userName)
                .password(password)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
