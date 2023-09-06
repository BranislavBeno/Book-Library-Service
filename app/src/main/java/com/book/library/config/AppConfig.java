package com.book.library.config;

import com.book.library.book.BookRepository;
import com.book.library.book.BookService;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.ReaderRepository;
import com.book.library.repository.BookFileRepository;
import com.book.library.repository.BookFileRepositoryImpl;
import com.book.library.service.BookFileService;
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
    public BookFileRepository bookFileRepository(@Value("${book.repository.path}") String path) {
        return new BookFileRepositoryImpl(path);
    }

    @Bean
    public BookFileService bookFileService(
            @Autowired BookFileRepository repository, @Value("${book.service.page.size:20}") int pageSize) {
        return new BookFileService(repository, pageSize);
    }

    @Bean
    public BookService bookService(
            @Autowired BookRepository bookRepository,
            @Autowired BorrowedBookRepository borrowedBookRepository,
            @Autowired ReaderRepository readerRepository,
            @Value("${book.service.page.size:20}") int pageSize) {
        return new BookService(bookRepository, borrowedBookRepository, readerRepository, pageSize);
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
