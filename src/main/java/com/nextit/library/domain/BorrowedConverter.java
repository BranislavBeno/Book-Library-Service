package com.nextit.library.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Converter
public class BorrowedConverter implements AttributeConverter<Borrowed, String> {

    private static final Logger logger = LoggerFactory.getLogger(BorrowedConverter.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Borrowed attribute) {
        String dbData = null;
        try {
            dbData = objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }
        return dbData;
    }

    @Override
    public Borrowed convertToEntityAttribute(String dbData) {
        Borrowed attribute = null;
        try {
            attribute = objectMapper.readValue(dbData, Borrowed.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }
        return attribute;
    }
}
