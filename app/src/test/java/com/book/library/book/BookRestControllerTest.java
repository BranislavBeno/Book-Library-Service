package com.book.library.book;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.library.controller.AbstractControllerTest;
import com.book.library.util.BookUtils;
import io.micrometer.observation.tck.TestObservationRegistry;
import java.util.stream.Stream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@EnableTestObservation
class BookRestControllerTest extends AbstractControllerTest {

    @Autowired
    private BookService service;

    @Autowired
    private TestObservationRegistry registry;

    @Nested
    @Sql(scripts = "/sql/init_db.sql")
    @Sql(scripts = "/sql/clean_up_db.sql", executionPhase = AFTER_TEST_METHOD)
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
                            "readerId": 1,
                            "from": "2023-01-05"
                        }""";
        private static final String BAD_REQUEST_BODY_5 =
                """
                        {
                            "bookId": 10,
                            "readerId": 1,
                            "from": "2023-01-05"
                        }""";
        private static final String BAD_REQUEST_BODY_6 = BookUtils.createNonValidBorrowRequest();

        @Autowired
        private MockMvc mockMvc;

        @ParameterizedTest
        @CsvSource(value = {"all,1,1", "all,2,0", "available,0,2", "borrowed,0,4"})
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/book/" + endpoint)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .param("page", page)
                            .with(oidcLogin()))
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
                    .perform(post("/api/v1/book/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status);
        }

        private static Stream<Arguments> creationRequests() {
            return Stream.of(
                    Arguments.of(BAD_REQUEST_BODY_1, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_2, status().isBadRequest()),
                    Arguments.of(BAD_REQUEST_BODY_3, status().isBadRequest()),
                    Arguments.of(REQUEST_BODY_1, status().isOk()));
        }

        @ParameterizedTest
        @MethodSource("updateRequests")
        void testUpdatingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
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

        @ParameterizedTest
        @MethodSource("borrowRequests")
        void testBorrowingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status);
        }

        private static Stream<Arguments> borrowRequests() {
            return Stream.of(
                    Arguments.of(REQUEST_BODY_2, status().isOk()),
                    Arguments.of(BAD_REQUEST_BODY_5, status().isNotFound()),
                    Arguments.of(BAD_REQUEST_BODY_6, status().isBadRequest()));
        }

        @ParameterizedTest
        @MethodSource("availRequests")
        void testAvailingBook(String id, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/avail")
                            .param("bookId", id)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status);
        }

        private static Stream<Arguments> availRequests() {
            return Stream.of(Arguments.of("10", status().isNotFound()), Arguments.of("1", status().isOk()));
        }

        @ParameterizedTest
        @MethodSource("deleteRequests")
        void testDeletingBook(String id, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(delete("/api/v1/book/delete")
                            .param("bookId", id)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status);
        }

        private static Stream<Arguments> deleteRequests() {
            return Stream.of(
                    Arguments.of("10", status().isNotFound()),
                    Arguments.of("1", status().isForbidden()),
                    Arguments.of("4", status().isOk()));
        }

        @Test
        void testForbidAddingBook() throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(REQUEST_BODY_1)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        void testForbidUpdatingBook() throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/update")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(REQUEST_BODY_1)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testForbidBorrowingBook() throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/borrow")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(REQUEST_BODY_2)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testForbidAvailingBook() throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/book/avail")
                            .param("bookId", "1")
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testForbidDeletingBook() throws Exception {
            this.mockMvc
                    .perform(delete("/api/v1/book/delete")
                            .param("bookId", "1")
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class EmptyBookListTest {

        @Autowired
        private MockMvc mockMvc;

        @ParameterizedTest
        @CsvSource(value = {"all,0,0", "available,0,0", "borrowed,0,0"})
        void testFindAll(String endpoint, String page, int size) throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/book/" + endpoint)
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
