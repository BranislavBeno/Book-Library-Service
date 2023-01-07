package com.nextit.library.controller;

import com.nextit.library.config.AppConfig;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@Import(AppConfig.class)
class BookRestControllerTest {

    private static final String BAD_REQUEST_1 = """
            {
               "id": 1,
                "name": "Very long book name",
                "author": "John Doe"
            }""";
    private static final String BAD_REQUEST_2 = """
            {
                "id": 1,
                "name": "",
                "author": "John Doe"
            }""";
    private static final String BAD_REQUEST_3 = """
            {
                "id": 1,
                "name": "Book name",
                "author": ""
            }""";
    private static final String BAD_REQUEST_4 = """
            {
            "id": 10,
            "name": "Book name",
            "author": "John Doe"
            }""";
    private static final String REQUEST = """
            {
            "id": 1,
            "name": "Book name",
            "author": "John Doe"
            }""";
    @Autowired
    private BookService service;
    @Autowired
    private BookMapper mapper;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BookListTest {

        @Autowired
        private MockMvc mockMvc;

        @DynamicPropertySource
        static void properties(DynamicPropertyRegistry registry) {
            registry.add("book.repository.path", () -> "src/test/resources/Library.xml");
        }

        @Order(1)
        @ParameterizedTest
        @CsvSource(value = {
                "all,1,1",
                "all,2,0",
                "available,0,2",
                "borrowed,0,4"
        })
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/books/" + endpoint)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .param("page", page))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(size)))
                    .andDo(print())
                    .andReturn();
        }

        @ParameterizedTest
        @MethodSource("creationRequests")
        void testAddingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(post("/api/v1/books/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status);
        }

        private static Stream<Arguments> creationRequests() {
            return Stream.of(
                    Arguments.of(BAD_REQUEST_1, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_2, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_3, status().isBadRequest()),
                    Arguments.of(REQUEST, status().isCreated())
            );
        }

        @ParameterizedTest
        @MethodSource("updateRequests")
        void testSuccessfulUpdate(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/books/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status);
        }

        private static Stream<Arguments> updateRequests() {
            return Stream.of(
                    Arguments.of(BAD_REQUEST_1, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_2, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_3, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_4, status().isBadRequest()),
                    Arguments.of(REQUEST, status().isOk())
            );
        }
    }

    @Nested
    class EmptyBookListTest {

        @Autowired
        private MockMvc mockMvc;

        @DynamicPropertySource
        static void properties(DynamicPropertyRegistry registry) {
            registry.add("book.repository.path", () -> "src/test/resources/Empty.xml");
        }

        @ParameterizedTest
        @CsvSource(value = {
                "all,0,0",
                "available,0,0",
                "borrowed,0,0"
        })
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/books/" + endpoint)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .param("page", page))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(size)))
                    .andDo(print())
                    .andReturn();
        }
    }
}