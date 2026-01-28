package com.book.library.recommendation;

import com.fasterxml.jackson.annotation.JsonCreator;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@DynamoDbBean
public class TestNotification {

    private String notificationId;
    private String email;
    private String bookInfo;
    private String token;

    public TestNotification() {}

    @JsonCreator
    public TestNotification(String json) throws JacksonException {
        ObjectMapper objectMapper = new ObjectMapper();
        TestNotification notification = objectMapper.readValue(json, TestNotification.class);
        this.notificationId = notification.notificationId;
        this.email = notification.email;
        this.bookInfo = notification.bookInfo;
        this.token = notification.token;
    }

    @DynamoDbPartitionKey
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDbAttribute("bookInfo")
    public String getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(String bookInfo) {
        this.bookInfo = bookInfo;
    }

    @DynamoDbAttribute("token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
