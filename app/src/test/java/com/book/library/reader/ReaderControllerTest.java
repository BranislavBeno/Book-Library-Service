package com.book.library.reader;

import com.book.library.AbstractTestResources;
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

class ReaderControllerTest extends AbstractTestResources {

    @Autowired
    private ReaderService service;

    @Nested
    @Sql(scripts = "/sql/init_db.sql")
    @Sql(scripts = "/sql/clean_up_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class ReaderListTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        void testForbidShowingAllReaders() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/all")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testShowingAllReaders() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/all")
                            .param("page", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("all-readers"))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readers", "pageNumbers"));
        }

        @Test
        void testForbidShowingAddReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-add")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testShowingAddReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-add")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-reader"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readerDto"));
        }

        @Test
        void testForbidShowingUpdateReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-update")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void testShowingUpdateReaderForm() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/show-update")
                            .param("readerId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-reader"))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readerDto"));
        }

        @Test
        void testForbidSavingReader() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/reader/save")
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @ParameterizedTest
        @CsvSource({"'',Doe,john@example.com", "John,'',john@example.com", "John,Doe,''", "John,Doe,john"})
        void testRejectingSavingReader(String firstName, String lastName, String email) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/reader/save")
                            .param("firstName", firstName)
                            .param("lastName", lastName)
                            .param("email", email)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("save-reader"))
                    .andExpect(MockMvcResultMatchers.model().hasErrors())
                    .andExpect(MockMvcResultMatchers.model().attributeHasErrors("readerDto"));
        }

        @Test
        void testSavingReader() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.post("/reader/save")
                            .param("firstName", "John")
                            .param("lastName", "Doe")
                            .param("email", "john@example.com")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/reader/all"));
        }

        @Test
        void testForbidDeletingReader() throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/delete")
                            .param("readerId", "1")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @ParameterizedTest
        @CsvSource(value = {"1,true", "5,false"})
        void testDeletingReader(String readerId, boolean forbidden) throws Exception {
            this.mockMvc
                    .perform(MockMvcRequestBuilders.get("/reader/delete")
                            .param("readerId", readerId)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(MockMvcResultMatchers.header().string("Location", "/reader/all"))
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
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .with(SecurityMockMvcRequestPostProcessors.oidcLogin()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.view().name("all-readers"))
                    .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                    .andExpect(MockMvcResultMatchers.model().attributeExists("readers", "pageNumbers"));
        }
    }
}
