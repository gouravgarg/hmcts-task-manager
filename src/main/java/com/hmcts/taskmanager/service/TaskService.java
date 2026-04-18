package com.hmcts.taskmanager.service;

import com.hmcts.taskmanager.domain.*;
import com.hmcts.taskmanager.dto.*;
import com.hmcts.taskmanager.exception.*;
import com.hmcts.taskmanager.mapper.TaskMapper;
import com.hmcts.taskmanager.repository.TaskRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskService {

    private final TaskRepository repository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        log.debug("Validating task creation request");
        validateDueDate(request.dueDate());

        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        log.info("Saving task to database: {}", request.title());
        return TaskMapper.toResponse(repository.save(task));
    }

    public TaskResponse getTask(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskMapper.toResponse(task);
    }

    public Page<TaskResponse> getTasks(int page, int size, TaskStatus status) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Task> tasks = (status != null)
                ? repository.findByStatus(status, pageable)
                : repository.findAll(pageable);

        return tasks.map(TaskMapper::toResponse);
    }

    public TaskResponse updateStatus(Long id, UpdateTaskStatusRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(request.status());

        return TaskMapper.toResponse(repository.save(task));
    }

    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate == null) return;

        if (dueDate.isBefore(LocalDate.now().plusDays(2))) {
            throw new InvalidTaskException("Due date must be at least 2 days in the future");
        }
    }
}