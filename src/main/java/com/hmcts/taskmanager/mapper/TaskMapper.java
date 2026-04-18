package com.hmcts.taskmanager.mapper;

import com.hmcts.taskmanager.domain.Task;
import com.hmcts.taskmanager.dto.TaskResponse;

public class TaskMapper {

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }
}