package ru.practicum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
  @Query(
      "select count(p) from ParticipationRequest p where p.event = ?1 and p.status = 'CONFIRMED'")
  Long countByEvent(Event event);

  @Query(
      "select count(p) from ParticipationRequest p where p.event.id = ?1 and p.status = 'CONFIRMED'")
  Long countByEventId(Long eventId);

  @Query("select p from ParticipationRequest p where p.event in ?1")
  List<ParticipationRequest> findAllByEvents(List<Event> events);

  List<ParticipationRequest> findAllByEvent(Event event);

  List<ParticipationRequest> findAllByUser(User user);

  Optional<ParticipationRequest> findByUserAndEvent(User user, Event event);
}
