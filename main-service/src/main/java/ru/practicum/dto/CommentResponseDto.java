package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CommentResponseDto {
  private String comment;
  private Long userId;
  private Long eventId;
  private String created;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String updated;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String updatedBy;
}
