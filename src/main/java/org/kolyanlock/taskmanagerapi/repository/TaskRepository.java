package org.kolyanlock.taskmanagerapi.repository;

import org.kolyanlock.taskmanagerapi.entity.Task;
import org.kolyanlock.taskmanagerapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = "description")
    Optional<Task> findByIdAndAuthor(Long taskId, User currentUser);

    Page<Task> findAllByAuthor(User currentUser, Pageable pageable);

    void deleteByAuthorId(Long userId);
}
