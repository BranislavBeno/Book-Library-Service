package com.book.library.recommendation;

import com.book.library.AbstractTestResources;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Sql(scripts = "/sql/init_db.sql")
@Sql(scripts = "/sql/clean_up_db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class BookRecommendationControllerTest extends AbstractTestResources {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRecommendationService service;

    @Test
    void testFailingRecommendBook() throws Exception {
        Mockito.when(service.recommendBookTo(1, 1)).thenThrow(new IllegalArgumentException());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/book/1/recommend/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", "failure"));
    }

    @Test
    void testSuccessfulRecommendBook() throws Exception {
        Mockito.when(service.recommendBookTo(1, 1)).thenReturn("");
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/book/1/recommend/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oidcLogin()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", "success"));
    }

    @Test
    void testBookRecommendationConfirmation() throws Exception {
        int bookId = 1;
        int readerId = 1;
        Mockito.when(service.confirmRecommendation("mike@b-l-s.click", bookId, readerId, "token"))
                .thenReturn(true);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/book/%d/recommend/%d/confirm".formatted(bookId, readerId))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.oidcLogin())
                        .param("token", "token"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", "danger"));
    }
}
