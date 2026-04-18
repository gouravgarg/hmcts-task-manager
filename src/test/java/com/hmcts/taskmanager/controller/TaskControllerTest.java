package com.hmcts.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmcts.taskmanager.domain.TaskStatus;
import com.hmcts.taskmanager.dto.*;
import com.hmcts.taskmanager.exception.GlobalExceptionHandler;
import com.hmcts.taskmanager.exception.InvalidTaskException;
import com.hmcts.taskmanager.exception.TaskNotFoundException;
import com.hmcts.taskmanager.service.TaskService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-level tests for Task APIs.
 *
 * Scope:
 * - Validates HTTP layer (request/response)
 * - Verifies interaction with service layer (mocked)
 * - Ensures correct status codes and payload structure
 *
 * Note:
 * - Uses @WebMvcTest (slice test)
 * - Imports GlobalExceptionHandler explicitly
 */
@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService service;

    @Autowired
    private ObjectMapper objectMapper;

    // =====================================================
    // CREATE TASK
    // =====================================================

    @Test
    @DisplayName("POST /tasks - Should create a task successfully and return 200 with response body")
    void shouldCreateTaskSuccessfully() throws Exception {

        // Given
        CreateTaskRequest request =
                new CreateTaskRequest("Test Task", "Description", null);

        TaskResponse response =
                new TaskResponse(1L, "Test Task", "Description",
                        TaskStatus.TODO, null, null);

        Mockito.when(service.createTask(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    // =====================================================
    // GET TASK BY ID
    // =====================================================

    @Test
    @DisplayName("GET /tasks/{id} - Should return task when task exists")
    void shouldReturnTaskById() throws Exception {

        // Given
        TaskResponse response =
                new TaskResponse(1L, "Task", null,
                        TaskStatus.TODO, null, null);

        Mockito.when(service.getTask(anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    // =====================================================
    // UPDATE STATUS
    // =====================================================

    @Test
    @DisplayName("PATCH /tasks/{id}/status - Should update task status successfully")
    void shouldUpdateTaskStatus() throws Exception {

        String json = """
        {
          "status": "DONE"
        }
        """;

        TaskResponse response =
                new TaskResponse(1L, "Task", null,
                        TaskStatus.DONE, null, null);

        Mockito.when(service.updateStatus(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/tasks/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())   // 👈 DEBUG
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    // =====================================================
    // DELETE TASK
    // =====================================================

    @Test
    @DisplayName("DELETE /tasks/{id} - Should delete task successfully")
    void shouldDeleteTaskSuccessfully() throws Exception {

        // Given
        Mockito.doNothing().when(service).deleteTask(anyLong());

        // When & Then
        mockMvc.perform(delete("/tasks/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /tasks - Should return 400 when title is blank")
    void shouldReturnBadRequest_whenTitleIsBlank() throws Exception {

        String json = """
        {
          "title": "",
          "description": "Test"
        }
        """;

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /tasks/{id} - Should return 404 when task not found")
    void shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {

        Mockito.when(service.getTask(1L))
                .thenThrow(new TaskNotFoundException(1L));

        mockMvc.perform(get("/tasks/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with id: 1"));
    }

    @Test
    @DisplayName("PATCH /tasks/{id}/status - Should return 400 for invalid status")
    void shouldReturnBadRequest_whenInvalidStatusProvided() throws Exception {

        String json = """
        {
          "status": "INVALID_STATUS"
        }
        """;

        mockMvc.perform(patch("/tasks/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /tasks/{id}/status - Should return 400 when body is missing")
    void shouldReturnBadRequest_whenRequestBodyMissing() throws Exception {

        mockMvc.perform(patch("/tasks/{id}/status", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks - Should return 400 when due date is invalid")
    void shouldReturnBadRequest_whenDueDateInvalid() throws Exception {

        String json = """
        {
          "title": "Test",
          "dueDate": "2026-01-01"
        }
        """;

        Mockito.when(service.createTask(any()))
                .thenThrow(new InvalidTaskException("Due date must be at least 2 days in the future"));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Due date must be at least 2 days in the future"));
    }
}