package com.example.todo.repository;

import com.example.todo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /** 完了フラグでフィルタリング */
    List<Task> findByCompletedOrderByCreatedAtDesc(Boolean completed);

    /** 全件を作成日時の降順で取得 */
    List<Task> findAllByOrderByCreatedAtDesc();

    /** タイトルまたは本文にキーワードを含むタスクを検索 */
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(
            String titleKeyword, String descriptionKeyword);

    /** タイトルまたは本文にキーワードを含み、完了フラグが一致するタスクを検索 */
    @Query("SELECT t FROM Task t WHERE t.completed = :completed " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Task> searchByKeywordAndCompleted(@Param("keyword") String keyword, @Param("completed") Boolean completed);
}
