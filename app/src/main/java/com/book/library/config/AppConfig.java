package com.book.library.config;

import com.book.library.book.BookRepository;
import com.book.library.book.BookService;
import com.book.library.book.BorrowedBookRepository;
import com.book.library.reader.ReaderRepository;
import com.book.library.reader.ReaderService;
import com.book.library.recommendation.BookRecommendationListener;
import com.book.library.recommendation.BookRecommendationRequestRepository;
import com.book.library.recommendation.BookRecommendationService;
import com.book.library.recommendation.DefaultBookRecommendationListener;
import com.book.library.tracing.TraceDao;
import com.book.library.user.*;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
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
    public SqsTemplate sqsTemplate(SqsAsyncClient sqsAsyncClient) {
        return SqsTemplate.newTemplate(sqsAsyncClient);
    }

    @Bean
    public BookRecommendationService bookRecommendationService(
            @Autowired BorrowedBookRepository borrowedBookRepository,
            @Autowired BookRepository bookRepository,
            @Autowired ReaderRepository readerRepository,
            @Autowired BookRecommendationRequestRepository requestRepository,
            @Autowired SqsTemplate sqsTemplate,
            @Value("${custom.recommendation-queue}") String queueName) {
        return new BookRecommendationService(
                borrowedBookRepository, bookRepository, readerRepository, requestRepository, sqsTemplate, queueName);
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-real-sqs-listener", havingValue = "true")
    public BookRecommendationListener bookRecommendationListener(
            @Autowired BookRecommendationService service,
            @Autowired MailSender sender,
            @Value("${custom.auto-confirm-recommendations}") boolean autoConfirmRecommendation,
            @Value("${custom.confirm-email-from-address}") String confirmEmailFromAddress,
            @Value("${custom.external-url}") String externalUrl) {
        return new DefaultBookRecommendationListener(
                service, sender, autoConfirmRecommendation, confirmEmailFromAddress, externalUrl);
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

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public UserService cognitoUserService(
            @Autowired CognitoIdentityProviderClient identityProvider,
            @Value("${spring.security.oauth2.client.registration.cognito.poolId}") String userPoolId,
            @Value("${spring.security.oauth2.client.registration.cognito.clientId}") String clientId,
            @Value("${spring.security.oauth2.client.registration.cognito.clientSecret}") String clientSecret) {
        return new CognitoUserService(identityProvider, userPoolId, clientId, clientSecret);
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
    public UserService localUserService() {
        return new LocalUserService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public LogoutSuccessHandler cognitoOidcLogoutSuccessHandler(
            @Value("${spring.security.oauth2.client.registration.cognito.clientId}") String clientId,
            @Value("${spring.security.oauth2.client.registration.cognito.logoutUrl}") String userPoolLogoutUrl) {
        return new CognitoOidcLogoutSuccessHandler(userPoolLogoutUrl, clientId);
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");

        return successHandler;
    }

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public RegistrationService registrationService(
            @Autowired CognitoIdentityProviderClient identityProvider,
            @Autowired MeterRegistry meterRegistry,
            @Value("${spring.security.oauth2.client.registration.cognito.poolId}") String userPoolId) {
        return new CognitoRegistrationService(identityProvider, meterRegistry, userPoolId);
    }

    @Bean
    public TraceDao traceDao(
            @Autowired DynamoDbClient dynamoDbClient, @Value("${custom.tracing-table}") String tableName) {
        return new TraceDao(dynamoDbClient, tableName);
    }
}
