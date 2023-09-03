package com.book.library.book;

import static com.book.library.util.BookUtils.getTomorrowsDate;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.library.dto.BookMapper;
import com.book.library.service.BookFileService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class BookControllerTest extends AbstractControllerTest {

    @Autowired
    private BookFileService service;

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
        @CsvSource(
                value = {
                    "/,index,1,true",
                    "/,index,2,false",
                    "/available,available-books,0,true",
                    "/borrowed,borrowed-books,0,true"
                })
        void testShowingBookList(String url, String viewName, String page, boolean found) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url).param("page", page).with(oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", found))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }

        @Order(2)
        @Test
        void testShowAddBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/addBook")
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("availableBookDto"));
        }

        @Order(3)
        @Test
        void testShowUpdateBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/updateBook")
                            .param("bookId", "1")
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("availableBookDto"));
        }

        @Order(4)
        @Test
        void testShowBorrowBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/borrowBook")
                            .param("bookId", "1")
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("borrow-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("borrowedDto"));
        }

        @Order(5)
        @Test
        void testRejectingSavingBook() throws Exception {
            this.mockMvc
                    .perform(post("/save")
                            .param("name", "Very long book name")
                            .param("author", "John Doe")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("save-book"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasErrors("availableBookDto"));
        }

        @Order(6)
        @Test
        void testSavingBook() throws Exception {
            this.mockMvc
                    .perform(post("/save")
                            .param("name", "Book name")
                            .param("author", "John Doe")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/"));
        }

        @Order(7)
        @Test
        void testAvailingBook() throws Exception {
            this.mockMvc
                    .perform(get("/avail")
                            .param("bookId", "1")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/borrowed"));
        }

        @Order(8)
        @Test
        void testRejectingBorrowingBook() throws Exception {
            String date = getTomorrowsDate().toString();

            this.mockMvc
                    .perform(post("/borrow")
                            .param("firstName", "Paul")
                            .param("lastName", "Newman")
                            .param("from", date)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("borrow-book"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasErrors("borrowedDto"));
        }

        @Order(9)
        @Test
        void testBorrowingBook() throws Exception {
            this.mockMvc
                    .perform(post("/borrow")
                            .param("bookId", "1")
                            .param("firstName", "Paul")
                            .param("lastName", "Newman")
                            .param("from", "2023-01-05")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/available"));
        }

        @Order(10)
        @Test
        void testDeletingBook() throws Exception {
            this.mockMvc
                    .perform(get("/delete")
                            .param("bookId", "1")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/"));
        }

        @Test
        void testForbidShowAddBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/addBook").with(oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidShowUpdateBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/updateBook")
                            .param("bookId", "1")
                            .with(oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidShowBorrowBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/borrowBook")
                            .param("bookId", "1")
                            .with(oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidSavingBook() throws Exception {
            this.mockMvc
                    .perform(post("/save")
                            .param("name", "Book name")
                            .param("author", "John Doe")
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testForbidAvailingBook() throws Exception {
            this.mockMvc
                    .perform(get("/avail").param("bookId", "1").with(csrf()).with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testForbidDeletingBook() throws Exception {
            this.mockMvc
                    .perform(get("/delete").param("bookId", "1").with(csrf()).with(oidcLogin()))
                    .andExpect(status().isForbidden());
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
        @CsvSource(value = {"/,index", "/available,available-books", "/borrowed,borrowed-books"})
        void testShowingEmptyBookList(String url, String viewName) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url).with(oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }
    }
}
