package com.pagejump.scrumboardwebclient.service;

import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.exception.InvalidTaskRequestException;
import com.pagejump.scrumboardwebclient.exception.TaskAlreadyDeletedException;
import com.pagejump.scrumboardwebclient.exception.TaskNotFoundException;
import com.pagejump.scrumboardwebclient.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    /*
    * References for error handling:
    * https://www.baeldung.com/spring-webclient-get-response-body
    * https://medium.com/swlh/spring-boot-webclient-cheat-sheet-5be26cfa3e
    * https://medium.com/nerd-for-tech/webclient-error-handling-made-easy-4062dcf58c49
    */

    private WebClient webClient;

    // For testing.
    public ScrumBoardService(String baseURL) {
        this.webClient = WebClient.create(baseURL);
    }

    public List<Task> getAllTasks() {
        return webClient.get()
                .retrieve()
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new RuntimeException("Server is not responding."))
                    )
                .bodyToFlux(Task.class)
                .collectList()
                .block();
    }

    public Task getTaskById(long taskId) {
        return webClient.get()
                .uri("/" + taskId)
                .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            error -> error.bodyToMono(String.class).map(TaskNotFoundException::new)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new RuntimeException("Server is not responding."))
                    )
                .bodyToMono(Task.class)
                .block();
    }

    public Task createTask(TaskRequestDTO taskRequestDTO) {
        log.info("I've entered the createTask function with the ff info.: {}", taskRequestDTO.toString() );
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(taskRequestDTO))
                .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> error.bodyToMono(String.class).map(InvalidTaskRequestException::new)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new RuntimeException("Server is not responding."))
                    )
                .bodyToMono(Task.class)
                .block();
    }

    public void deleteTask(long taskId) {
        webClient.delete()
                .uri("/" + taskId)
                .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            error -> error.bodyToMono(String.class).map(TaskNotFoundException::new)
                    )
                    .onStatus(HttpStatus.BAD_REQUEST::equals,
                            error -> error.bodyToMono(String.class).map(TaskAlreadyDeletedException::new)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new RuntimeException("Server is not responding."))
                    )
                .bodyToMono(Void.class)
                .block();
    }

    public Task updateTask(long taskId, TaskRequestDTO taskRequestDTO) {
        return webClient.put()
                .uri("/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(taskRequestDTO))
                .retrieve()
                    .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals,
                            error -> error.bodyToMono(String.class).map(InvalidTaskRequestException::new)
                    )
                    .onStatus(HttpStatus.NOT_FOUND::equals,
                            error -> error.bodyToMono(String.class).map(TaskNotFoundException::new)
                    )
                    .onStatus(HttpStatus.BAD_REQUEST::equals,
                            error -> error.bodyToMono(String.class).map(TaskAlreadyDeletedException::new)
                    )
                    .onStatus(HttpStatusCode::is5xxServerError,
                            error -> Mono.error(new RuntimeException("Server is not responding."))
                    )
                .bodyToMono(Task.class)
                .block();
    }
}
