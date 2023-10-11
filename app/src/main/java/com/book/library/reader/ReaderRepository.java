package com.book.library.reader;

import com.book.library.dto.ReaderDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {

    @Query(
            """
                    SELECT new com.book.library.dto.ReaderDto(r.id, r.firstName, r.lastName, r.email)
                    FROM Reader r""")
    List<ReaderDto> findAllReaders();

    @Query(
            """
                    SELECT new com.book.library.dto.ReaderDto(r.id, r.firstName, r.lastName, r.email)
                    FROM Reader r""")
    Page<ReaderDto> findAllReadersPaged(Pageable pageable);

    @Query(
            """
                    SELECT new com.book.library.dto.ReaderDto(r.id, r.firstName, r.lastName, r.email)
                    FROM Reader r WHERE r.id = :id""")
    Optional<ReaderDto> findReaderById(long id);
}
