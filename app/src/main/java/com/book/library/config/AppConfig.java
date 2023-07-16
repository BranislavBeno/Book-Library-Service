package com.book.library.config;

import com.book.library.dto.BookMapper;
import com.book.library.repository.BookFileRepository;
import com.book.library.repository.BookRepository;
import com.book.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@EnableConfigurationProperties(CustomConfigurationProperties.class)
public class AppConfig {

    @Bean
    public BookRepository bookRepository(@Value("${book.repository.path}") String path) {
        return new BookFileRepository(path);
    }

    @Bean
    public BookService bookService(
            @Autowired BookRepository repository, @Value("${book.service.page.size:20}") int pageSize) {
        return new BookService(repository, pageSize);
    }

    @Bean
    public BookMapper bookMapper() {
        return new BookMapper();
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public CognitoIdentityProviderClient cognitoIdentityProvider(
            AwsRegionProvider regionProvider, AwsCredentialsProvider awsCredentialsProvider) {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(regionProvider.getRegion())
                .build();
    }
}
