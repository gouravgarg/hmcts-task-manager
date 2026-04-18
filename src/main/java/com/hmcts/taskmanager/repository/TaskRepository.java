package com.hmcts.taskmanager.repository;

import com.hmcts.taskmanager.domain.Task;
import com.hmcts.taskmanager.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}