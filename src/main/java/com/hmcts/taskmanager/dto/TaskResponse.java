package com.hmcts.taskmanager.dto;

import com.hmcts.taskmanager.domain.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        LocalDateTime createdAt
) {}