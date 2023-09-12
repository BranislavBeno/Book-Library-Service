package com.book.library.reader;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.library.book.BookService;
import com.book.library.book.EnableTestObservation;
import com.book.library.controller.AbstractControllerTest;
import io.micrometer.observation.tck.TestObservationRegistry;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@EnableTestObservation
class ReaderRestControllerTest extends AbstractControllerTest {

    @Autowired
    private BookService service;

    @Autowired
    private TestObservationRegistry registry;

    @Nested
    @Sql(scripts = "/sql/init_db.sql")
    @Sql(scripts = "/sql/clean_up_db.sql", executionPhase = AFTER_TEST_METHOD)
    class ReaderListTest {

        private static final String BAD_REQUEST_BODY_1 =
                """
                        {
                        "firstName": "",
                        "lastName": "Doe",
                        "email": "john@example.com"
                        }""";

        private static final String BAD_REQUEST_BODY_2 =
                """
                        {
                        "firstName": "John",
                        "lastName": "",
                        "email": "john@example.com"
                        }""";

        private static final String BAD_REQUEST_BODY_3 =
                """
                        {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john"
                        }""";

        private static final String REQUEST_BODY_1 =
                """
                        {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john@example.com"
                        }""";

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testForbidFindingAll() throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/reader/all")
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testFindAll() throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/reader/all")
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(5)))
                    .andDo(print())
                    .andReturn();
        }

        @Test
        void testForbidAddingReader() throws Exception {
            this.mockMvc
                    .perform(put("/api/v1/reader/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(REQUEST_BODY_1)
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @ParameterizedTest
        @MethodSource("creationRequests")
        void testAddingBook(String body, ResultMatcher status) throws Exception {
            this.mockMvc
                    .perform(post("/api/v1/reader/add")
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
    }

    @Nested
    class EmptyReaderListTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testFindAll() throws Exception {
            this.mockMvc
                    .perform(get("/api/v1/reader/all")
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is(200))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()", is(0)))
                    .andDo(print())
                    .andReturn();
        }
    }
}
