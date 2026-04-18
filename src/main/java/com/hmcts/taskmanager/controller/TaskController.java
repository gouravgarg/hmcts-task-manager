package com.hmcts.taskmanager.controller;

import com.hmcts.taskmanager.domain.TaskStatus;
import com.hmcts.taskmanager.dto.*;
import com.hmcts.taskmanager.service.TaskService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService service;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        log.info("Creating task with title: {}", request.title());
        return service.createTask(request);
    }

    @GetMapping
    public Page<TaskResponse> getTasks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "15") int size,
            @RequestParam(name = "status", required = false) TaskStatus status
    ) {
        return service.getTasks(page, size, status);
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable("id") Long id) {
        return service.getTask(id);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse updateStatus(
            @PathVariable("id") Long id,
            @RequestBody UpdateTaskStatusRequest request) {

        return service.updateStatus(id, request);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        service.deleteTask(id);
    }
}