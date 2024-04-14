package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequestDto {
    private List<Long> requestIds;
    private String status;
}