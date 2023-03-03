package com.pagejump.scrumboardwebclient.service;

import com.pagejump.scrumboardwebclient.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
}
