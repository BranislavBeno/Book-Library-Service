package com.book.library.reader;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.book.library.book.BookService;
import com.book.library.book.EnableTestObservation;
import com.book.library.controller.AbstractControllerTest;
import io.micrometer.observation.tck.TestObservationRegistry;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@EnableTestObservation
class ReaderRestControllerTest extends AbstractControllerTest {

    @Autowired
    private BookService service;

    @Autowired
    private TestObservationRegistry registry;

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
