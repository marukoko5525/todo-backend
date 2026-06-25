package com.example.todo.repository;

import com.example.todo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** 完了フラグでフィルタリング */
    List<Task> findByCompletedOrderByCreatedAtDesc(Boolean completed);

    /** 全件を作成日時の降順で取得 */
    List<Task> findAllByOrderByCreatedAtDesc();
}
