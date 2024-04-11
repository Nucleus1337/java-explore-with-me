package ru.practicum.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.model.enums.ParticipationRequestStatus;

@Entity
@Table(name = "participant_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ParticipationRequestStatus status;

  private LocalDateTime created;

  @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(targetEntity = Event.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "event_id")
  private Event event;
}
