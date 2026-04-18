package com.hmcts.taskmanager.dto;

import com.hmcts.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;


public record UpdateTaskStatusRequest(
        @NotNull TaskStatus status
) {}