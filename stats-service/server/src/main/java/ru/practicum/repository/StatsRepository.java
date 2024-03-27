package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
  @Query(
      value =
          "select app, uri, count(ip) hits "
              + "from view_statistics "
              + "where created between ?1 and ?2 "
              + "group by app, uri "
              + "order by count(ip) desc",
      nativeQuery = true)
  List<ViewStats> findAll(LocalDateTime start, LocalDateTime end);

  @Query(
      value =
          "select app, uri, count(ip) hits "
              + "from view_statistics "
              + "where created between ?1 and ?2 "
              + "and uri in ?3 "
              + "group by app, uri "
              + "order by count(ip) desc",
      nativeQuery = true)
  List<ViewStats> findAllInUris(LocalDateTime start, LocalDateTime end, String[] uris);

  @Query(
      value =
          "select app, uri, count(distinct ip) hits "
              + "from view_statistics "
              + "where created between ?1 and ?2 "
              + "group by app, uri "
              + "order by count(ip) desc",
      nativeQuery = true)
  List<ViewStats> findAllUnique(LocalDateTime start, LocalDateTime end);

  @Query(
      value =
          "select app, uri, count(distinct ip) hits "
              + "from view_statistics "
              + "where created between ?1 and ?2 "
              + "and uri in ?3 "
              + "group by app, uri "
              + "order by count(ip) desc",
      nativeQuery = true)
  List<ViewStats> findAllUniqueInUris(LocalDateTime start, LocalDateTime end, String[] uris);
}
