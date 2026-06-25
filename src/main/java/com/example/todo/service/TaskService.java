package com.example.todo.service;

import com.example.todo.dto.TaskRequest;
import com.example.todo.dto.TaskResponse;

import java.util.List;

public interface TaskService {

    /** 全タスク取得 (completed=null で全件、true/false でフィルタ) */
    List<TaskResponse> findAll(Boolean completed);

    /** 1件取得 */
    TaskResponse findById(Long id);

    /** 新規作成 */
    TaskResponse create(TaskRequest request);

    /** 更新 */
    TaskResponse update(Long id, TaskRequest request);

    /** 削除 */
    void delete(Long id);
}
