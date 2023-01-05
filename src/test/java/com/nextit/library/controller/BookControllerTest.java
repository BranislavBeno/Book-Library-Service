package com.nextit.library.controller;

import com.nextit.library.domain.Book;
import com.nextit.library.dto.BookMapper;
import com.nextit.library.service.BookService;
import com.nextit.library.util.BookUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService service;
    @MockBean
    private BookMapper mapper;

    @ParameterizedTest
    @CsvSource(value = {"/,index", "/available,available-books", "/borrowed,borrowed-books"})
    void testShowingEmptyBookList(String url, String viewName) throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(viewName))
                .andExpect(MockMvcResultMatchers.model().attribute("found", false))
                .andExpect(MockMvcResultMatchers.model().attributeExists("books"));
    }

    @Test
    void testShowingAllBooks() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(BookUtils.createBook()));
        Mockito.when(mapper.toBookDto(Mockito.any(Book.class))).thenReturn(BookUtils.createBookDto());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                .andExpect(MockMvcResultMatchers.model().attributeExists("books"));
    }

    @Test
    void testShowingAvailableBooks() throws Exception {
        Mockito.when(service.findAllAvailable()).thenReturn(List.of(BookUtils.createBook()));
        Mockito.when(mapper.toAvailableDto(Mockito.any(Book.class))).thenReturn(BookUtils.createBookAvailableDto());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/available"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("available-books"))
                .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                .andExpect(MockMvcResultMatchers.model().attributeExists("books"));
    }

    @Test
    void testShowingBorrowedBooks() throws Exception {
        Mockito.when(service.findAllBorrowed()).thenReturn(List.of(BookUtils.createBook()));
        Mockito.when(mapper.toBorrowedDto(Mockito.any(Book.class))).thenReturn(BookUtils.createBookBorrowedDto());

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/borrowed"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("borrowed-books"))
                .andExpect(MockMvcResultMatchers.model().attribute("found", true))
                .andExpect(MockMvcResultMatchers.model().attributeExists("books"));
    }
}