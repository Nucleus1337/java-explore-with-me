package ru.practicum.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
  @Query("select c from Compilation c where c.pinned = coalesce(:pinned, c.pinned)")
  Optional<List<Compilation>> findAllCompilation(@Param("pinned") Boolean pinned, Pageable pageable);
}
