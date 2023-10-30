package com.book.library.recommendation;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import com.book.library.AbstractTestResources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Sql(scripts = "/sql/init_db.sql")
@Sql(scripts = "/sql/clean_up_db.sql", executionPhase = AFTER_TEST_METHOD)
class BookRecommendationControllerTest extends AbstractTestResources {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRecommendationService service;

    @ParameterizedTest
    @CsvSource({"1,1,success", "1,10,failure", "10,1,failure"})
    void testRecommendBook(int bookId, int readerId, String result) throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/book/%d/recommend/%d".formatted(bookId, readerId))
                        .with(csrf())
                        .with(oidcLogin()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(header().string("Location", "/book/borrowed"))
                .andExpect(MockMvcResultMatchers.flash().attribute("messageType", result));
    }
}
