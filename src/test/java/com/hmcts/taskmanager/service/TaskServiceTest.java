package com.hmcts.taskmanager.service;

import com.hmcts.taskmanager.domain.*;
import com.hmcts.taskmanager.dto.*;
import com.hmcts.taskmanager.exception.*;
import com.hmcts.taskmanager.repository.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task", "Desc", LocalDate.now().plusDays(3));

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Test Task");

        when(repository.save(any(Task.class))).thenReturn(saved);

        var response = service.createTask(request);

        assertEquals("Test Task", response.title());
        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldThrowException_whenDueDateInvalid() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, LocalDate.now().plusDays(1));

        assertThrows(InvalidTaskException.class,
                () -> service.createTask(request));
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task");

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        var response = service.getTask(1L);

        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowNotFound_whenTaskMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> service.getTask(1L));
    }

    @Test
    void shouldUpdateStatus() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TODO);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any())).thenReturn(task);

        var response = service.updateStatus(1L,
                new UpdateTaskStatusRequest(TaskStatus.DONE));

        assertEquals(TaskStatus.DONE, response.status());
    }

    @Test
    void shouldDeleteTask() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteTask(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldThrow_whenDeletingNonExistingTask() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class,
                () -> service.deleteTask(1L));
    }

    @Test
    void shouldReturnPaginatedTasks() {
        Task task = new Task();
        task.setId(1L);

        Page<Task> page = new PageImpl<>(java.util.List.of(task));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        var result = service.getTasks(0, 15, null);

        assertEquals(1, result.getTotalElements());
    }
}