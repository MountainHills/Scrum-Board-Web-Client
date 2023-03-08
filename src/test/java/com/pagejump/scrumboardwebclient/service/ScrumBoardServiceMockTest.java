package com.pagejump.scrumboardwebclient.service;

import com.google.gson.Gson;
import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.model.Task;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ScrumBoardServiceMockTest {
    /*
    * References:
    * https://www.youtube.com/watch?v=GBKY8QyfNDk
    * https://github.com/eugenp/tutorials/tree/master/spring-reactive-modules/spring-5-reactive-client
    * https://www.baeldung.com/spring-mocking-webclient
    */

    private static MockWebServer mockWebServer;
    private ScrumBoardService scrumBoardService;
    private Gson gson = new Gson();

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseURL = String.format("http://localhost:%s", mockWebServer.getPort());
        scrumBoardService = new ScrumBoardService(baseURL);
    }

    @Test
    void getAllTasks() throws InterruptedException {
        List<Task> responseList = Stream.of(
                new Task(1, "Task 1", "Description 1", "TODO", false, "2023-02-27T17:55:53.790283", "2023-02-27T17:55:53.790326"),
                new Task(2, "Task 2", "Description 2", "TODO", false, "2023-02-27T18:00:12.757744", "2023-03-01T16:28:42.373168"))
                .collect(Collectors.toList());
        String response = gson.toJson(responseList);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(response)
        );

        List<Task> taskList =  scrumBoardService.getAllTasks();

        // Checks the values
        assertEquals(taskList, responseList);

        // Checks the request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/", request.getPath());
    }

    @Test
    void getTaskById() throws InterruptedException {
        long sampleId = 8;
        Task sampleTask = new Task(sampleId, "Task 8", "Description 8", "TODO", false, "2023-02-27T17:55:53.790283", "2023-02-27T17:55:53.790326");
        String response = gson.toJson(sampleTask);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(response)
        );

        Task actualTask = scrumBoardService.getTaskById(sampleId);

        // Check values
        assertEquals(actualTask, sampleTask);

        // Checks the request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/" + sampleId, request.getPath());
    }

    @Test
    void createTask() throws InterruptedException {
        String titleRequest = "Created Task";
        String descriptionRequest = "Created description";
        String statusRequest = "TODO";

        long responseId = 15;

        TaskRequestDTO taskRequest = new TaskRequestDTO(titleRequest, descriptionRequest, statusRequest);
        Task taskResponse = new Task(responseId, titleRequest, descriptionRequest, statusRequest, false, "2023-02-27T18:00:53.790283", "2023-02-27T18:00:53.790326");
        String response = gson.toJson(taskResponse);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(201)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(response)
        );

        Task createdTask = scrumBoardService.createTask(taskRequest);

        // Check values
        assertEquals(createdTask, taskResponse);

        // Checks the request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/" , request.getPath());
    }

    @Test
    void deleteTask() throws InterruptedException {

        long requestId = 12;

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(204)
        );

        scrumBoardService.deleteTask(requestId);

        // Checks the request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/" + requestId, request.getPath());
    }

    @Test
    void updateTask() throws InterruptedException {
        String titleUpdateRequest = "Updated Task";
        String descriptionUpdateRequest = "Updated Description";
        String statusUpdateRequest = "DONE";
        long taskId = 8;

        TaskRequestDTO taskUpdateRequest = new TaskRequestDTO(titleUpdateRequest, descriptionUpdateRequest, statusUpdateRequest);
        Task taskReponse = new Task(taskId, titleUpdateRequest, descriptionUpdateRequest, statusUpdateRequest, false, "2023-02-27T18:00:53.790283", "2023-02-27T18:30:53.790283");

        String response = gson.toJson(taskReponse);

        mockWebServer.enqueue(
                new MockResponse().setResponseCode(202)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(response)
        );

        Task updatedTask = scrumBoardService.updateTask(taskId, taskUpdateRequest);

        // Check values
        // assertEquals(updatedTask, taskReponse);
        assertEquals(updatedTask.getTitle(), titleUpdateRequest);
        assertEquals(updatedTask.getDescription(), descriptionUpdateRequest);
        assertEquals(updatedTask.getStatus(), statusUpdateRequest);
        assertNotEquals(updatedTask.getCreationTime(), updatedTask.getUpdateTime());

        // Checks the request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/" + taskId, request.getPath());
    }
}