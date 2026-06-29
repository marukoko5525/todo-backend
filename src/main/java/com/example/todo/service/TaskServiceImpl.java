package com.example.todo.service;

import com.example.todo.dto.TaskRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.entity.Task;
import com.example.todo.exception.TaskNotFoundException;
import com.example.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public List<TaskResponse> findAll(Boolean completed) {
        List<Task> tasks = (completed == null)
                ? taskRepository.findAllByOrderByCreatedAtDesc()
                : taskRepository.findByCompletedOrderByCreatedAtDesc(completed);
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Override
    public List<TaskResponse> search(String keyword, Boolean completed) {
        List<Task> tasks = (completed == null)
                ? taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword)
                : taskRepository.searchByKeywordAndCompleted(keyword, completed);
        return tasks.stream().map(TaskResponse::from).toList();
    }

    @Override
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .build();
        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (request.getTitle() != null)       task.setTitle(request.getTitle());
        if (request.getDescription() != null)  task.setDescription(request.getDescription());
        if (request.getCompleted() != null)    task.setCompleted(request.getCompleted());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
}
