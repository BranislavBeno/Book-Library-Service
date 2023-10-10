package com.book.library.repository;

import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractTestRepository<T> extends BaseTestRepository {

    protected abstract JpaRepository<T, Integer> getRepository();

    protected void assertEntity(int id, Consumer<T> consumer) {
        Optional<T> entity = getRepository().findById(id);
        entity.ifPresentOrElse(consumer, () -> Assertions.fail("Entity not found"));
    }

    protected static PageRequest getPageRequest() {
        return PageRequest.of(0, 5);
    }
}
