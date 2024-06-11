package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReaderServiceTest {

    @Mock
    private ReaderRepository repository;

    @Mock
    private Page<ReaderDto> readerPage;

    @Mock
    private Reader reader;

    private ReaderService cut;

    @BeforeEach
    void setUp() {
        cut = new ReaderService(repository, 5);
    }

    @Test
    void findAllReaders() {
        Mockito.when(repository.findAllReadersPaged(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(readerPage);
        cut.findAllReaders(1);
        Mockito.verify(repository).findAllReadersPaged(ArgumentMatchers.any(PageRequest.class));
    }

    @Test
    void saveReader() {
        Mockito.when(repository.save(ArgumentMatchers.any(Reader.class))).thenReturn(reader);
        cut.saveReader(reader);
        Mockito.verify(repository).save(ArgumentMatchers.any(Reader.class));
    }

    @ParameterizedTest
    @CsvSource({"1,true", "2,false"})
    void readerExists(int id, boolean found) {
        Mockito.when(repository.existsById(id)).thenReturn(found);
        cut.readerExists(id);
        Mockito.verify(repository).existsById(id);
    }

    @Test
    void findReader() {
        int id = 1;
        Mockito.when(repository.findById(id)).thenReturn(Optional.ofNullable(ArgumentMatchers.any(Reader.class)));
        cut.findReader(id);
        Mockito.verify(repository).findById(id);
    }

    @Test
    void deleteReader() {
        int id = 1;
        Mockito.doNothing().when(repository).deleteById(id);
        cut.deleteReader(id);
        Mockito.verify(repository).deleteById(id);
    }
}
