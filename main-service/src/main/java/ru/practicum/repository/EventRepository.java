package ru.practicum.repository;

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

//  List<Event> findAllById(Long[] ids);

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
              + "where lower(e.annotation) ilike concat('%', cast(:text as text), '%') "
              + "      or lower(e.description) ilike concat('%', cast(:text as text), '%') "
              + "      or :text is null "
              + "and e.state = 'PUBLISHED' "
              + "and coalesce (:paid, e.paid) = e.paid "
              + "and (category_id in (:categories) or :categories is null) "
              + "and (e.event_date between to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss') and to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss') or :rangeStart is null and :rangeEnd is null) "
              + "and (:onlyAvailable = false or r.cnt < e.participant_limit and :onlyAvailable = true) "
              /*+ "order by :sort"*/)
  List<Event> findAllWithFilters(
      @Param("text") String text,
      @Param("paid") Boolean paid,
      @Param("categories") List<Long> categories,
      @Param("rangeStart") String rangeStart,
      @Param("rangeEnd") String rangeEnd,
      @Param("onlyAvailable") Boolean onlyAvailable,
      Pageable pageable);

  @Query(
      nativeQuery = true,
      value =
          "select e.* \n"
              + "from events e \n"
              + "where (:categories is null or category_id in (:categories)) \n"
              + "and (:users is null or user_id in (:users)) \n"
              + "and (:states is null or state in (:states)) \n"
              + "and (:rangeStart is null or to_timestamp(:rangeStart, 'yyyy-mm-dd hh24:mi:ss') <= e.event_date) \n"
              + "and (:rangeEnd is null or to_timestamp(:rangeEnd, 'yyyy-mm-dd hh24:mi:ss') >= e.event_date) \n"
              + "order by id")
  List<Event> findAllWithFilters(
      @Param("users") List<Long> users,
      @Param("states") List<String> states,
      @Param("categories") List<Long> categories,
      @Param("rangeStart") String rangeStart,
      @Param("rangeEnd") String rangeEnd,
      Pageable pageable);
}
