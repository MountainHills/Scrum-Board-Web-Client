package com.pagejump.scrumboardwebclient.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.exception.InvalidTaskRequestException;
import com.pagejump.scrumboardwebclient.exception.TaskAlreadyDeletedException;
import com.pagejump.scrumboardwebclient.exception.TaskNotFoundException;
import com.pagejump.scrumboardwebclient.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ScrumBoardServiceIntegrationTest {
    /*
     * References:
     * https://dzone.com/articles/7-popular-unit-test-naming - Naming Conventions
     * https://www.baeldung.com/spring-webclient-get-response-body
     * https://stackoverflow.com/questions/4105795/pretty-print-json-in-java
     */

    // Constants
    private final String DOES_NOT_EXIST_ID = "21ce319b-0eae-41d5-b25a-016122d5798b"; // Random UUID. It doesn't exist.
    private final String DOES_EXIST_ID = "3ac096ec-1282-4e76-b1b0-4923a5aa05eb";
    private final String DELETE_ID = "afb67416-8fd6-4484-8bb6-94be533cfe66";
    private final String UPDATE_ID = "87dc9fa0-af6f-4078-b084-31de854056bc";
    private final String ALREADY_DELETED_ID = "e9dd1432-d788-4553-a56f-3783fc0e9371";
    @Autowired
    private ScrumBoardService scrumBoardService;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void createTask_ValidTaskRequest_Task() {
        TaskRequestDTO sampleCreateTask = new TaskRequestDTO(
                "Created Title 3", "Created description 3", "TODO");
        Task sampleCreatedTaskResponse = scrumBoardService.createTask(sampleCreateTask);

        assertNotNull(sampleCreatedTaskResponse);
        log.info("The task was created with the following information: {}", gson.toJson(sampleCreatedTaskResponse));
    }

    @Test
    void createTask_InvalidTaskStatus_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidStatus = new TaskRequestDTO(
                "Something", "Something here", "INVALID STATUS");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.createTask(taskWithInvalidStatus),
                "Invalid status inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void createTask_InvalidTitle_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidTitle = new TaskRequestDTO(
                null, "Something", "TODO");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.createTask(taskWithInvalidTitle),
                "Invalid title inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void getAllTasks() {
        List<Task> taskList = scrumBoardService.getAllTasks();

        assertNotNull(taskList);
        log.info("The contents are: {}", gson.toJson(taskList));
    }

    @Test
    void getTaskById_DoesExists_Task() {
        Task sampleTask = scrumBoardService.getTaskById(DOES_EXIST_ID);

        assertNotNull(sampleTask);
        log.info("The task with id = {} has the following information: {}", DOES_EXIST_ID, gson.toJson(sampleTask));
    }

    @Test
    void getTaskById_DoesNotExist_ExceptionThrown() {
        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.getTaskById(DOES_NOT_EXIST_ID),
                "Invalid ids should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void deleteTask_DoesExist_Void() {
        assertDoesNotThrow(() -> scrumBoardService.deleteTask(DELETE_ID));
        log.info("The task with id = {} has been deleted", DELETE_ID);
    }

    @Test
    void deleteTask_DoesNotExist_ExceptionThrown() {
        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.deleteTask(DOES_NOT_EXIST_ID),
                "Any tasks that can't be found should throw an error.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void deleteTask_DeletedTask_ExceptionThrown() {
        Exception e = assertThrows(TaskAlreadyDeletedException.class,
                () -> scrumBoardService.deleteTask(ALREADY_DELETED_ID),
                "Any tasks that is already deleted and tries to be deleted again should throw an error.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask() {
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO(
                "Updated Task", "Updated Description", "DONE");

        Task sampleUpdateTaskResponse = scrumBoardService.updateTask(UPDATE_ID, sampleUpdateTask);

        assertNotNull(sampleUpdateTaskResponse);
        log.info("Update task with id = {}. The new updated task now contains: {}",
                DOES_EXIST_ID, gson.toJson(sampleUpdateTaskResponse));
    }

    @Test
    void updateTask_InvalidTaskStatus_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidStatus = new TaskRequestDTO(
                "Something", "Something here", "INVALID STATUS");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.updateTask(UPDATE_ID, taskWithInvalidStatus),
                "Invalid status inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_InvalidTitle_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidTitle = new TaskRequestDTO(
                null, "Something", "TODO");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.updateTask(UPDATE_ID, taskWithInvalidTitle),
                "Invalid title inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_DeletedTask_ExceptionThrown() {
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO(
                "Updated Task", "Updated Description", "DONE");

        Exception e = assertThrows(TaskAlreadyDeletedException.class,
                () -> scrumBoardService.updateTask(ALREADY_DELETED_ID, sampleUpdateTask),
                "Any updates to a deleted task should be thrown.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_DoesNotExist_ExceptionThrown() {
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO(
                "Updated Task", "Updated Description", "DONE");

        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.updateTask(DOES_NOT_EXIST_ID, sampleUpdateTask),
                "Any invalid task id should throw.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void sampleTest() {
        // Create Task 1
        TaskRequestDTO createTask1 = new TaskRequestDTO(
                "Complete Sample Task 1", "Complete Sample 1", "TODO");
        Task taskCreated1 = scrumBoardService.createTask(createTask1);

        assertNotNull(taskCreated1);
        log.info("The task was created with the following information: {}", gson.toJson(taskCreated1));

        // Create Task 2
        TaskRequestDTO createTask2 = new TaskRequestDTO(
                "Complete Sample 2", "Complete Sample 2", "TODO");
        Task taskCreated2 = scrumBoardService.createTask(createTask2);

        assertNotNull(taskCreated2);
        log.info("The task was created with the following information: {}", gson.toJson(taskCreated2));

        assertNotEquals(taskCreated1, taskCreated2);

        // Updates the first created task.
        String updateTaskId = taskCreated1.getId();
        TaskRequestDTO updateTaskRequest = new TaskRequestDTO(
                "Updated the Created Task", "Updated the Description", "DONE");
        Task updatedTask = scrumBoardService.updateTask(updateTaskId, updateTaskRequest);

        assertNotNull(updatedTask);
        log.info("Update task with id = {}. The new updated task now contains: {}", updateTaskId, gson.toJson(updatedTask));

        // Deletes the second created task.
        assertDoesNotThrow(() -> scrumBoardService.deleteTask(taskCreated2.getId()));
        log.info("The task with id = {} has been deleted", taskCreated2.getId());

        // Get All Tasks (Both deleted and not deleted)
        List<Task> taskList = scrumBoardService.getAllTasks();
        log.info("The contents are: {}", gson.toJson(taskList));

        // Get ID of an existing task.
        Task existingTask = scrumBoardService.getTaskById(taskCreated1.getId());
        log.info("The task with id = {} has the following information: {}", taskCreated1.getId(), gson.toJson(existingTask));

        // Get ID that does not exist. Ex. 1000
        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.getTaskById(DOES_NOT_EXIST_ID)
        );

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }
}