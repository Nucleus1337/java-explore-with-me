package ru.practicum.model;

import java.time.LocalDateTime;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String annotation;

  @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  private String description;
  private LocalDateTime eventDate;
  private Double lat;
  private Double lon;
  private Boolean paid;
  private Long participantLimit;
  private Boolean requestModeration;
  private String title;
  private LocalDateTime created;

  @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  private LocalDateTime published;

  @Enumerated(EnumType.STRING)
  private EventState state;

  @ManyToOne(targetEntity = Compilation.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "compilation_id")
  private Compilation compilation;
}
