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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * This class calls API calls in the SCRUM Board Server Application
 * @author antonbondoc
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScrumBoardService {
    /*
     * References for error handling:
     * https://www.baeldung.com/spring-webclient-get-response-body
     * https://medium.com/swlh/spring-boot-webclient-cheat-sheet-5be26cfa3e
     * https://medium.com/nerd-for-tech/webclient-error-handling-made-easy-4062dcf58c49
     */

    final private WebClient scrumBoardClient;

    /**
     * Gets all the available tasks in the server application
     *
     * @return Returns a list of Task models.
     */
    public List<Task> getAllTasks() {
        return scrumBoardClient.get()
                .retrieve()
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding."))
                )
                .bodyToFlux(Task.class)
                .collectList()
                .block();
    }

    public List<Task> getAllTasksErrorMap() {
        return scrumBoardClient.get()
                .retrieve()
                .bodyToFlux(Task.class)
                .onErrorMap(WebClientResponseException.class,
                        error -> new RuntimeException(error.getMessage())
                )
                .collectList()
                .block();
    }

    /**
     * Gets the task from the server application using UUID in String format.
     *
     * @param taskId which is the UUID in String format.
     * @return Returns a singular Task model
     */
    public Task getTaskById(String taskId) {
        return scrumBoardClient.get()
                .uri("/" + taskId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        error -> error.bodyToMono(String.class).map(TaskNotFoundException::new)
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding."))
                )
                .onStatus(
                        HttpStatusCode::isError,
                        error -> Mono.error(new RuntimeException(error.toString()))
                )
                .bodyToMono(Task.class)
                .block();
    }

    public Task getTaskByIdErrorMap(String taskId) {
        return scrumBoardClient.get()
                .uri("/" + taskId)
                .retrieve()
                .bodyToMono(Task.class)
                .onErrorMap(WebClientResponseException.class,
                        error -> {
                            HttpStatusCode status = error.getStatusCode();
                            String message = error.getResponseBodyAsString();
                            if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) throw new TaskNotFoundException(message);

                            throw new RuntimeException(error.getMessage());
                        }
                )
                .block();
    }

    public Task createTask(TaskRequestDTO taskRequestDTO) {
        return scrumBoardClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskRequestDTO), TaskRequestDTO.class)
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

    public Task createTaskErrorMap(TaskRequestDTO taskRequestDTO) {
        return scrumBoardClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskRequestDTO), TaskRequestDTO.class)
                .retrieve()
                .bodyToMono(Task.class)
                .onErrorMap(WebClientResponseException.class,
                        error -> {
                            HttpStatusCode status = error.getStatusCode();
                            if (status.isSameCodeAs(HttpStatus.UNPROCESSABLE_ENTITY))
                                throw new InvalidTaskRequestException(error.getResponseBodyAsString());

                            throw new RuntimeException(error.getMessage());
                        }
                )
                .block();
    }

    public void deleteTask(String taskId) {
        scrumBoardClient.delete()
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
                .toBodilessEntity()
                .block();
    }

    public void deleteTaskErrorMap(String taskId) {
        scrumBoardClient.delete()
                .uri("/" + taskId)
                .retrieve()
                .toBodilessEntity()
                .onErrorMap(WebClientResponseException.class,
                        error -> {
                            HttpStatusCode status = error.getStatusCode();
                            String message = error.getResponseBodyAsString();
                            if (status.is4xxClientError()) {
                                if (status.isSameCodeAs(HttpStatus.NOT_FOUND))
                                    throw new TaskNotFoundException(message);
                                if (status.isSameCodeAs(HttpStatus.BAD_REQUEST))
                                    throw new TaskAlreadyDeletedException(message);
                            }
                            throw new RuntimeException(error.getMessage());
                        }
                )
                .block();
    }

    public Task updateTask(String taskId, TaskRequestDTO taskRequestDTO) {
        return scrumBoardClient.put()
                .uri("/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskRequestDTO), TaskRequestDTO.class)
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

    public Task updateTaskErrorMap(String taskId, TaskRequestDTO taskRequestDTO) {
        return scrumBoardClient.put()
                .uri("/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(taskRequestDTO), TaskRequestDTO.class)
                .retrieve()
                .bodyToMono(Task.class)
                .onErrorMap(WebClientResponseException.class,
                        error -> {
                            HttpStatusCode status = error.getStatusCode();
                            String message = error.getResponseBodyAsString();
                            if (status.is5xxServerError()) {
                                if (status.isSameCodeAs(HttpStatus.UNPROCESSABLE_ENTITY))
                                    throw new InvalidTaskRequestException(message);
                                if (status.isSameCodeAs(HttpStatus.NOT_FOUND))
                                    throw new TaskNotFoundException(message);
                                if (status.isSameCodeAs(HttpStatus.BAD_REQUEST))
                                    throw new TaskAlreadyDeletedException(message);
                            }

                            throw new RuntimeException(error.getMessage());
                        }
                )
                .block();
    }
}
