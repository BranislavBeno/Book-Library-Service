package com.book.library.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.library.config.AppConfig;
import com.book.library.dto.BookMapper;
import com.book.library.service.BookService;
import com.book.library.util.BookUtils;
import io.micrometer.observation.tck.TestObservationRegistry;
import java.util.stream.Stream;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(BookRestController.class)
@Import(AppConfig.class)
@EnableAutoConfiguration
@EnableTestObservation
class BookRestControllerTest {

    @Autowired
    private BookService service;

    @Autowired
    private BookMapper mapper;

    @Autowired
    private TestObservationRegistry registry;

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class BookListTest {

        private static final String BAD_REQUEST_BODY_1 =
                """
                {
                   "id": 1,
                    "name": "Very long book name",
                    "author": "John Doe"
                }""";
        private static final String BAD_REQUEST_BODY_2 =
                """
                {
                    "id": 1,
                    "name": "",
                    "author": "John Doe"
                }""";
        private static final String BAD_REQUEST_BODY_3 =
                """
                {
                    "id": 1,
                    "name": "Book name",
                    "author": ""
                }""";
        private static final String BAD_REQUEST_BODY_4 =
                """
                {
                    "id": 10,
                    "name": "Book name",
                    "author": "John Doe"
                }""";
        private static final String REQUEST_BODY_1 =
                """
                {
                    "id": 1,
                    "name": "Book name",
                    "author": "John Doe"
                }""";
        private static final String REQUEST_BODY_2 =
                """
                {
                    "bookId": 4,
                    "firstName": "John",
                    "lastName": "Doe",
                    "from": "2023-01-05"
                }""";
        private static final String BAD_REQUEST_BODY_5 =
                """
                {
                    "bookId": 10,
                    "firstName": "John",
                    "lastName": "Doe",
                    "from": "2023-01-05"
                }""";
        private static final String BAD_REQUEST_BODY_6 = BookUtils.createNonValidBorrowRequest();

        @Autowired
        private MockMvc mockMvc;

        @DynamicPropertySource
        static void properties(DynamicPropertyRegistry registry) {
            registry.add("book.repository.path", () -> "src/test/resources/Library.xml");
        }

        @Order(1)
        @ParameterizedTest
        @CsvSource(value = {"all,1,1", "all,2,0", "available,0,2", "borrowed,0,4"})
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/books/" + endpoint)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .param("page", page)
                            .with(oidcLogin()))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(size)))
                    .andDo(print())
                    .andReturn();
        }

        @Order(2)
        @ParameterizedTest
        @MethodSource("creationRequests")
        void testAddingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(post("/api/v1/books/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status);
        }

        private static Stream<Arguments> creationRequests() {
            return Stream.of(
                    Arguments.of(BAD_REQUEST_BODY_1, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_2, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_3, status().isBadRequest()),
                    Arguments.of(REQUEST_BODY_1, status().isOk()));
        }

        @Order(3)
        @ParameterizedTest
        @MethodSource("updateRequests")
        void testUpdatingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/books/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status);
        }

        private static Stream<Arguments> updateRequests() {
            return Stream.of(
                    Arguments.of(BAD_REQUEST_BODY_1, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_2, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_3, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_4, status().isNotFound()),
                    Arguments.of(REQUEST_BODY_1, status().isOk()));
        }

        @Order(4)
        @ParameterizedTest
        @MethodSource("borrowRequests")
        void testBorrowingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/books/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status);
        }

        private static Stream<Arguments> borrowRequests() {
            return Stream.of(
                    Arguments.of(REQUEST_BODY_2, status().isOk()),
                    Arguments.of(BAD_REQUEST_BODY_5, status().isNotFound()),
                    Arguments.of(BAD_REQUEST_BODY_6, status().isBadRequest()));
        }

        @Order(5)
        @ParameterizedTest
        @MethodSource("availRequests")
        void testAvailingBook(String id, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/books/avail")
                            .param("bookId", id)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status);
        }

        private static Stream<Arguments> availRequests() {
            return Stream.of(Arguments.of("10", status().isNotFound()), Arguments.of("1", status().isOk()));
        }

        @Order(6)
        @ParameterizedTest
        @MethodSource("deleteRequests")
        void testDeletingBook(String id, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(delete("/api/v1/books/delete")
                            .param("bookId", id)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status);
        }

        private static Stream<Arguments> deleteRequests() {
            return Stream.of(Arguments.of("10", status().isNotFound()), Arguments.of("1", status().isOk()));
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
        @CsvSource(value = {"all,0,0", "available,0,0", "borrowed,0,0"})
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/books/" + endpoint)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .param("page", page)
                            .with(oidcLogin()))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(size)))
                    .andDo(print())
                    .andReturn();
        }
    }
}
