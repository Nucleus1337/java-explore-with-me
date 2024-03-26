package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
    List<Hit> findAll(LocalDateTime start, LocalDateTime end, boolean unique);
    List<Hit> findAll(LocalDateTime start, LocalDateTime end, boolean unique, String[] uris);
}
