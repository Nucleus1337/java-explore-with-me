package ru.practicum.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("select new ru.practicum.dto.CategoryDto(c.id, c.name) from Category c")
  Optional<List<CategoryDto>> findAllCategories(Pageable pageable);
}
