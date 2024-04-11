package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Category;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {
  Optional<List<Event>> findByUserOrderByCreated(User user, Pageable pageable);

  List<Event> findByCompilation(Compilation compilation);

  @Query("select e from Event e where e.compilation in ?1")
  List<Event> findAllByCompilation(List<Compilation> compilations);

  List<Event> findAllById(Long[] ids);

  @Modifying
  @Query("update Event e set e.compilation = :compilation where id in :ids")
  void updateCompilation(@Param("compilation") Compilation compilation, @Param("ids") Long[] ids);

  @Query(
      "select e from Event e "
          + "where e.state = 'PUBLISHED' "
          + "and coalesce(:paid, e.paid) = e.paid "
          + "and (e.category in :categories or :categories is null) "
          + "and (lower(e.description) like lower(concat('%', :text, '%')) "
          + "or lower(e.annotation) like lower(concat('%', :text, '%')) "
          + "or :text is null) ")
  List<Event> findAllWithSomeFilters(
      @Param("paid") Boolean paid,
      @Param("categories") List<Category> categories,
      @Param("text") String text);

  @Query(
      nativeQuery = true,
      value =
          "with requests_count as (select count(user_id) cnt, event_id "
              + "                  from participant_requests pr "
              + "                  where status = 'CONFIRM' "
              + "                  group by event_id) "
              + "select e.* "
              + "from events e "
              + "left join requests_count r on r.event_id = e.id "
              + "where lower(e.annotation) ilike concat('%', :text, '%') "
              + "      or lower(e.description) ilike concat('%', :text, '%') "
              + "      or :text is null "
              + "and e.state = 'PUBLISHED' "
              + "and coalesce (:paid, e.paid) = e.paid "
              + "and category_id in (:categories) "
              + "and (:rangeStart is null and :rangeEnd is null or :rangeStart <= e.event_date and :rangeEnd >= e.event_date) "
              + "and (:onlyAvailable = false or r.cnt < e.participant_limit and :onlyAvailable = true) "
              + "order by :sort")
  List<Event> findAllWithFilters(
      @Param("text") String text,
      @Param("paid") Boolean paid,
      @Param("categories") Long[] categories,
      @Param("rangeStart") LocalDateTime rangeStart,
      @Param("rangeEnd") LocalDateTime rangeEnd,
      @Param("onlyAvailable") Boolean onlyAvailable,
      @Param("sort") String sort,
      Pageable pageable);

  @Query(
      nativeQuery = true,
      value =
          "select e.*\n"
              + "from events e\n"
              + "where (category_id in (:categories) or :categories is null)\n"
              + "and (user_id in (:users) or :categories is null)\n"
              + "and (state in (:states) or :states is null)\n"
              + "and (:rangeStart is null or :rangeStart <= e.event_date) \n"
              + "and (:rangeEnd is null or :rangeEnd >= e.event_date)\n"
              + "order by id")
  List<Event> findAllWithFilters(
      @Param("users") Long[] users,
      @Param("states") String[] states,
      @Param("categories") Long[] categories,
      @Param("rangeStart") LocalDateTime rangeStart,
      @Param("rangeEnd") LocalDateTime rangeEnd,
      Pageable pageable);
}
