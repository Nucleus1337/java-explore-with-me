package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
  @Query(
      "select count(p) from ParticipationRequest p where p.event = ?1 and p.status = 'CONFIRMED'")
  Long countByEvent(Event event);

  @Query(
      "select count(p) from ParticipationRequest p where p.event.id = ?1 and p.status = 'CONFIRMED'")
  Long countByEventId(Long eventId);

  @Query(
          "select p from ParticipationRequest p where p.event in ?1"
  )
  List<ParticipationRequest> findAllByEvent(List<Event> events);
}
