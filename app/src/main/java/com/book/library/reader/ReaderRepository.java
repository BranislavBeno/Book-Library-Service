package com.book.library.reader;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {

    @Query(
            """
                    SELECT new com.book.library.reader.ReaderDto(r.id, r.firstName, r.lastName)
                    FROM Reader r""")
    Page<ReaderDto> findAllReaders(Pageable pageable);
}
