package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public record ReaderService(ReaderRepository repository, int pageSize) {

    public Page<ReaderDto> findAllReaders(int page) {
        return repository.findAllReadersPaged(getPageRequest(page));
    }

    public Reader saveReader(Reader reader) {
        return repository.save(reader);
    }

    public boolean readerExists(int id) {
        return repository.existsById(id);
    }

    @Nullable
    public Reader findReader(int id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteReader(int id) {
        repository.deleteById(id);
    }

    private PageRequest getPageRequest(int page) {
        return PageRequest.of(page, pageSize);
    }
}
