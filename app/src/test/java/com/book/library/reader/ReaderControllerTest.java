package com.book.library.reader;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import com.book.library.controller.AbstractControllerTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class ReaderControllerTest extends AbstractControllerTest {

    @Autowired
    private ReaderService service;

    @Nested
    @Sql(scripts = "/sql/init_db.sql")
    @Sql(scripts = "/sql/clean_up_db.sql", executionPhase = AFTER_TEST_METHOD)
    class ReaderListTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testForbidShowingAllReaders() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/all").with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testShowingAllReaders() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/all")
                            .param("page", "1")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("all-readers"))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readers", "pageNumbers"));
        }

        @Test
        void testForbidShowingAddReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-add").with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void testShowingAddReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-add")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-reader"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readerDto"));
        }

        @Test
        void testForbidSavingReader() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/reader/save").with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @CsvSource({"'',Doe,john@example.com", "John,'',john@example.com", "John,Doe,''", "John,Doe,john"})
        void testRejectingSavingReader(String firstName, String lastName, String email) throws Exception {
            this.mockMvc
                    .perform(post("/reader/save")
                            .param("firstName", firstName)
                            .param("lastName", lastName)
                            .param("email", email)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(view().name("save-reader"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasErrors("readerDto"));
        }

        @Test
        void testSavingReader() throws Exception {
            this.mockMvc
                    .perform(post("/reader/save")
                            .param("firstName", "John")
                            .param("lastName", "Doe")
                            .param("email", "john@example.com")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/reader/all"));
        }

        @Test
        void testForbidDeletingReader() throws Exception {
            this.mockMvc
                    .perform(get("/reader/delete")
                            .param("readerId", "1")
                            .with(csrf())
                            .with(oidcLogin()))
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest
        @CsvSource(value = {"1,true", "5,false"})
        void testDeletingReader(String readerId, boolean forbidden) throws Exception {
            this.mockMvc
                    .perform(get("/reader/delete")
                            .param("readerId", readerId)
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(header().string("Location", "/reader/all"))
                    .andExpect(MockMvcResultMatchers.flash().attribute("forbidden", forbidden));
        }
    }

    @Nested
    class EmptyReaderListTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testShowingEmptyReaderList() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/all")
                            .with(csrf())
                            .with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("all-readers"))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readers", "pageNumbers"));
        }
    }
}
