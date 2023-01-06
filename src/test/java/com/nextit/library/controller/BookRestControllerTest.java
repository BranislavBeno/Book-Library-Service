package com.nextit.library.controller;

import com.nextit.library.config.AppConfig;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@Import(AppConfig.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookService service;
    @MockBean
    private BookMapper mapper;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("book.repository.path", () -> "src/test/resources/Library.xml");
    }

    @Test
    void testFindAll() throws Exception {
        this.mockMvc
                .perform(get("/api/v1/books/all")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                        .param("page", "1"))
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(1)))
                .andDo(print())
                .andReturn();
    }
}