package com.pagejump.scrumboardwebclient.service;

import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrumBoardService {
    private WebClient webClient;

    // For testing.
    public ScrumBoardService(String baseURL) {
        this.webClient = WebClient.create(baseURL);
    }

    public List<Task> getAllTasks() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(Task.class)
                .collectList()
                .block();
    }

    public Task getTaskById(long taskId) {
        return webClient.get()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }

    // Not necessary.
    public Mono<Task> getTaskByIdAsync(long taskId) {
        return webClient.get()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Task.class);
    }

    // Not necessary.
    public Flux<Task> getAllTasksAsync() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(Task.class);
    }

    public Task createTask(TaskRequestDTO taskRequestDTO) {
        log.info("I've entered the createTask function with the ff info.: {}", taskRequestDTO.toString() );
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(taskRequestDTO))
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }

    public void deleteTask(long taskId) {
        log.info("I'm inside the deleteTask function.");
        webClient.delete()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Task updateTask(long taskId, TaskRequestDTO taskRequestDTO) {
        log.info("I'm inside the updateTask function with id = {}, and taskRequestDTO with ff info: {}", taskId, taskRequestDTO.toString());
        return webClient.put()
                .uri("/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(taskRequestDTO))
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }
}
