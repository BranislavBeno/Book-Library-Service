package com.book.library.reader;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
