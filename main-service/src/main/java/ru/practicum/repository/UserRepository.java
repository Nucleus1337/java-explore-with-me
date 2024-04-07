package ru.practicum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query(
      "select new ru.practicum.dto.UserDto(u.id, u.name, u.email)"
          + "from User u "
          + "where u.id in (:ids)")
  Optional<List<UserDto>> findUsersByIds(@Param("ids") List<Long> ids);
}
