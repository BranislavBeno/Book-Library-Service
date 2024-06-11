package com.book.library.book;

import com.book.library.AbstractTestResources;
import com.book.library.util.BookUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class BookControllerTest extends AbstractTestResources {

    @Autowired
    private BookService service;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Nested
    @Sql(scripts = "/sql/init_db.sql")
    @Sql(scripts = "/sql/clean_up_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class BookListTest {

        @Autowired
        private MockMvc mockMvc;

        @ParameterizedTest
        @CsvSource(
                value = {
                    "/book/all,all-books,1,true",
                    "/book/all,all-books,2,false",
                    "/book/available,available-books,0,true",
                    "/book/borrowed,borrowed-books,0,true"
                })
        void testShowingBookList(String url, String viewName, String page, boolean found) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url)
                            .param("page", page)
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", found))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }

        @Test
        void testShowAddBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-add")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("availableBookDto"));
        }

        @Test
        void testShowUpdateBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-update")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-book"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("availableBookDto"));
        }

        @Test
        void testShowBorrowBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-borrow")
                            .param("bookId", "1")
                            .param("readerId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("borrow-book"))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readers"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("borrowedDto"));
        }

        @ParameterizedTest
        @CsvSource({"'',John Doe", "Very long book name,John Doe", "Book name,''"})
        void testRejectingSavingBook(String name, String author) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/save")
                            .param("name", name)
                            .param("author", author)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-book"))
                    .andExpect(MockMvcResultMatchers.model().hasErrors())
                    .andExpect(MockMvcResultMatchers.model().attributeHasErrors("availableBookDto"));
        }

        @Test
        void testSavingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/save")
                            .param("name", "Book name")
                            .param("author", "John Doe")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/book/all"));
        }

        @Test
        void testAvailingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/avail")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/book/borrowed"));
        }

        @Test
        void testRejectingBorrowingBook() throws Exception {
            String date = BookUtils.getTomorrowDate().toString();

            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/borrow")
                            .param("bookId", "4")
                            .param("readerId", "1")
                            .param("from", date)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("borrow-book"))
                    .andExpect(MockMvcResultMatchers.model().hasErrors())
                    .andExpect(MockMvcResultMatchers.model().attributeHasErrors("borrowedDto"));
        }

        @ParameterizedTest
        @CsvSource({"1,1", "10,1", "4,10"})
        void testRefusingBorrowingBook(String bookId, String readerId) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/borrow")
                            .param("bookId", bookId)
                            .param("readerId", readerId)
                            .param("from", "2023-01-05")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.view().name("redirect:/book/available"));
        }

        @Test
        void testBorrowingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/borrow")
                            .param("bookId", "4")
                            .param("readerId", "1")
                            .param("from", "2023-01-05")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/book/available"));
        }

        @ParameterizedTest
        @CsvSource(value = {"1,true", "4,false"})
        void testDeletingBook(String bookId, boolean forbidden) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/delete")
                            .param("bookId", bookId)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/book/all"))
                    .andExpect(MockMvcResultMatchers.flash().attribute("forbidden", forbidden));
        }

        @Test
        void testForbidShowAddBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-add")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidShowUpdateBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-update")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidShowBorrowBookForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/show-borrow")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidSavingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/book/save")
                            .param("name", "Book name")
                            .param("author", "John Doe")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidAvailingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/avail")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testForbidDeletingBook() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/book/delete")
                            .param("bookId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }
    }

    @Nested
    class EmptyBookListTest {

        @Autowired
        private MockMvc mockMvc;

        @ParameterizedTest
        @CsvSource(value = {"/book/all,all-books", "/book/available,available-books", "/book/borrowed,borrowed-books"})
        void testShowingEmptyBookList(String url, String viewName) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get(url).with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name(viewName))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("books", "pageNumbers"));
        }
    }
}
