package ru.practicum.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {
  Optional<List<Event>> findByUser(User user, Pageable pageable);

  List<Event> findByCompilation(Compilation compilation);

  List<Event> findAllById(Long[] ids);

  @Modifying
  @Query("update Event e set e.compilation = :compilation where id in :ids")
  void updateCompilation(@Param("compilation") Compilation compilation, @Param("ids") Long[] ids);
}
