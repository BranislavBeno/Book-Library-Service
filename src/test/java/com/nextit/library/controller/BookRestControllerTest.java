package com.nextit.library.controller;

import com.nextit.library.config.AppConfig;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@Import(AppConfig.class)
class BookRestControllerTest {

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
        @ValueSource(strings = {
                """
                        {
                            "name": "Very long book name",
                            "author": "John Doe"
                        }""",
                """
                        {
                            "name": "",
                            "author": "John Doe"
                        }""",
                """
                        {
                            "name": "Book name",
                            "author": ""
                        }"""})
        void testFailingAdd(String body) throws Exception {
            this.mockMvc
                    .perform(post("/api/v1/books/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"""
                {
                "name": "Book name",
                "author": "John Doe"
                }"""})
        void testSuccessfulAdd(String body) throws Exception {
            this.mockMvc
                    .perform(post("/api/v1/books/add")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
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