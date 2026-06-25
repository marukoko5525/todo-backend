package com.example.todo;

import com.example.todo.dto.TaskRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.exception.TaskNotFoundException;
import com.example.todo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskResponse sampleResponse(Long id, String title, boolean completed) {
        return TaskResponse.builder()
                .id(id)
                .title(title)
                .description(null)
                .completed(completed)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/tasks — 一覧取得")
    void getAll() throws Exception {
        given(taskService.findAll(null))
                .willReturn(List.of(sampleResponse(1L, "買い物", false)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("買い物"))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — 1件取得")
    void getById() throws Exception {
        given(taskService.findById(1L)).willReturn(sampleResponse(1L, "買い物", false));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} — 存在しないIDは404")
    void getById_notFound() throws Exception {
        given(taskService.findById(99L)).willThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks — 新規作成")
    void create() throws Exception {
        TaskRequest req = new TaskRequest();
        req.setTitle("新しいタスク");

        given(taskService.create(any())).willReturn(sampleResponse(2L, "新しいタスク", false));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("新しいタスク"));
    }

    @Test
    @DisplayName("POST /api/tasks — タイトル未入力は400")
    void create_validationError() throws Exception {
        TaskRequest req = new TaskRequest();
        req.setTitle("");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} — 完了フラグ更新")
    void update() throws Exception {
        TaskRequest req = new TaskRequest();
        req.setTitle("更新済み");
        req.setCompleted(true);

        given(taskService.update(eq(1L), any()))
                .willReturn(sampleResponse(1L, "更新済み", true));

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} — 削除")
    void deleteTask() throws Exception {
        willDoNothing().given(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}