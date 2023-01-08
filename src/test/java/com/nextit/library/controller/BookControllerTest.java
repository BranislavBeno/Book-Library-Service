package com.nextit.library.controller;

import com.nextit.library.config.AppConfig;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(AppConfig.class)
class BookControllerTest {

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
        @WithMockUser(username = "user")
        @ParameterizedTest
        @CsvSource(value = {
                "/,index,1,true",
                "/,index,2,false",
                "/available,available-books,0,true",
                "/borrowed,borrowed-books,0,true"
        })
        void testShowingBookList(String url, String viewName, String page, boolean found) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url)
                            .param("page", page))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", found))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }

        @Order(2)
        @WithMockUser(username = "user")
        @Test
        void testShowAddBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/create"))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("add-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("availableBookDto"));
        }

        @Order(3)
        @WithMockUser(username = "user")
        @Test
        void testRejectingAddingNewBook() throws Exception {
            this.mockMvc
                    .perform(post("/add")
                            .param("name", "Very long book name")
                            .param("author", "John Doe")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("add-book"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasErrors("availableBookDto"));
        }

        @Order(4)
        @WithMockUser(username = "user")
        @Test
        void testAddingNewBook() throws Exception {
            this.mockMvc
                    .perform(post("/add")
                            .param("name", "Book name")
                            .param("author", "John Doe")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/"));
        }

        @Order(5)
        @WithMockUser(username = "user")
        @Test
        void testDeletingBook() throws Exception {
            this.mockMvc
                    .perform(get("/delete/1")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/"));
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

        @WithMockUser(username = "user")
        @ParameterizedTest
        @CsvSource(value = {"/,index", "/available,available-books", "/borrowed,borrowed-books"})
        void testShowingEmptyBookList(String url, String viewName) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }
    }
}