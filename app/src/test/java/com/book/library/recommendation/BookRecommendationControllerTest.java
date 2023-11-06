package com.book.library.recommendation;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.book.library.AbstractTestResources;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Sql(scripts = "/sql/init_db.sql")
@Sql(scripts = "/sql/clean_up_db.sql", executionPhase = AFTER_TEST_METHOD)
class BookRecommendationControllerTest extends AbstractTestResources {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRecommendationService service;

    @Test
    void testFailingRecommendBook() throws Exception {
        Mockito.when(service.recommendBookTo(1, 1)).thenThrow(new IllegalArgumentException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/book/1/recommend/1")
                        .with(csrf())
                        .with(oidcLogin()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", "failure"));
    }

    @Test
    void testSuccessfulRecommendBook() throws Exception {
        Mockito.when(service.recommendBookTo(1, 1)).thenReturn("");
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/book/1/recommend/1")
                        .with(csrf())
                        .with(oidcLogin()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", "success"));
    }

    @Disabled
    @ParameterizedTest
    @CsvSource({"mike@b-l-s.click,1,1,danger"})
    void testBookRecommendationConfirmation(String email, int bookId, int readerId, String status) throws Exception {
        Mockito.when(service.confirmRecommendation(email, bookId, readerId, "token"))
                .thenReturn(true);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/book/%d/recommend/%d/confirm".formatted(bookId, readerId))
                        .with(csrf())
                        .with(oidcLogin())
                        .param("token", "token"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", status));
    }
}
