package com.pagejump.scrumboardwebclient.service;

import com.pagejump.scrumboardwebclient.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrumBoardService {
    private final WebClient webClient;

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

    public Mono<Task> getTaskByIdAsync(long taskId) {
        return webClient.get()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Task.class);
    }

    public Flux<Task> getAllTasksAsync() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(Task.class);
    }

    public Task createTask(String requestBody) {
        return webClient.post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }

    public String deleteTask(long taskId) {
        webClient.delete()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Task.class);

        return "I deleted task with id = " + taskId;
    }

    public Task updateTask(long taskId, String requestBody) {
        return webClient.put()
                .uri("/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(Task.class)
                .block();
    }
}
