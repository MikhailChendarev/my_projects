package com.example.taskmanagmentsystem.repository;

import com.example.taskmanagmentsystem.enums.Priority;
import com.example.taskmanagmentsystem.enums.Status;
import com.example.taskmanagmentsystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorId(Long authorId, Pageable pageable);
    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);
    Page<Task> findByStatus(Status status, Pageable pageable);
    Page<Task> findByPriority(Priority priority, Pageable pageable);
}

