package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
  @Query(
      value =
          "select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) "
              + "from Hit h "
              + "where created between ?1 and ?2 "
              + "group by app, uri "
              + "order by count(ip) desc")
  List<ViewStatsDto> findAll(LocalDateTime start, LocalDateTime end);

  @Query(
      value =
          "select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(h.ip)) "
              + "from Hit h "
              + "where created between ?1 and ?2 "
              + "and uri in ?3 "
              + "group by app, uri "
              + "order by count(ip) desc")
  List<ViewStatsDto> findAllInUris(LocalDateTime start, LocalDateTime end, String[] uris);

  @Query(
      value =
          "select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) "
              + "from Hit h "
              + "where created between ?1 and ?2 "
              + "group by app, uri "
              + "order by count(ip) desc")
  List<ViewStatsDto> findAllUnique(LocalDateTime start, LocalDateTime end);

  @Query(
      value =
          "select new ru.practicum.dto.ViewStatsDto(h.app, h.uri, count(distinct h.ip)) "
              + "from Hit h "
              + "where created between ?1 and ?2 "
              + "and uri in ?3 "
              + "group by app, uri "
              + "order by count(ip) desc")
  List<ViewStatsDto> findAllUniqueInUris(LocalDateTime start, LocalDateTime end, String[] uris);
}
