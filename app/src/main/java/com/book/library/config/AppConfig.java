package com.book.library.config;

import com.book.library.book.BookRepository;
import com.book.library.book.BookService;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.ReaderRepository;
import com.book.library.reader.ReaderService;
import com.book.library.recommendation.BookRecommendationService;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@EnableConfigurationProperties(CustomConfigurationProperties.class)
public class AppConfig {

    @Bean
    public BookService bookService(
            @Autowired BookRepository bookRepository,
            @Autowired BorrowedBookRepository borrowedBookRepository,
            @Autowired ReaderRepository readerRepository,
            @Value("${book.service.page.size:20}") int pageSize) {
        return new BookService(bookRepository, borrowedBookRepository, readerRepository, pageSize);
    }

    @Bean
    public ReaderService readerService(
            @Autowired ReaderRepository readerRepository, @Value("${book.service.page.size:20}") int pageSize) {
        return new ReaderService(readerRepository, pageSize);
    }

    @Bean
    public BookRecommendationService bookRecommendationService(
            @Autowired BorrowedBookRepository borrowedBookRepository, @Autowired ReaderRepository readerRepository) {
        return new BookRecommendationService(borrowedBookRepository, readerRepository);
    }

    @Bean
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.newTemplate(sqsAsyncClient);
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
