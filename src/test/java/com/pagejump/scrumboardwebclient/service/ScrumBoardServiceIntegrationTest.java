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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ScrumBoardServiceIntegrationTest {
    /*
    * References:
    * https://dzone.com/articles/7-popular-unit-test-naming - Naming Conventions
    *
    */

    private ScrumBoardService scrumBoardService;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeEach
    void setup() {
        String baseURL = "http://localhost:8080/api/v1/tasks";
        scrumBoardService = new ScrumBoardService(baseURL);
    }

    @Test
    void createTask_ValidTaskRequest_Task() {
        TaskRequestDTO sampleCreateTask = new TaskRequestDTO("Created Title 10", "Created description 10", "TODO");
        Task sampleCreatedTaskResponse = scrumBoardService.createTask(sampleCreateTask);

        assertNotNull(sampleCreatedTaskResponse);
        log.info("The task was created with the following information: {}", gson.toJson(sampleCreatedTaskResponse));
    }

    @Test
    void createTask_InvalidTaskStatus_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidStatus = new TaskRequestDTO("Something", "Something here", "INVALID STATUS");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.createTask(taskWithInvalidStatus),
                "Invalid status inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void createTask_InvalidTitle_ExceptionThrown() {
        TaskRequestDTO taskWithInvalidTitle = new TaskRequestDTO(null, "Something", "TODO");

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
        long sampleId = 1;
        Task sampleTask = scrumBoardService.getTaskById(sampleId);

        assertNotNull(sampleTask);
        log.info("The task with id = {} has the following information: {}", sampleId, gson.toJson(sampleTask));
    }

    @Test
    void getTaskById_DoesNotExist_ExceptionThrown() {
        long invalidId = 1000;

        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.getTaskById(invalidId),
                "Invalid ids should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void deleteTask_DoesExist_Void() {
        long sampleDeleteTaskId = 12;
        assertDoesNotThrow(() -> scrumBoardService.deleteTask(sampleDeleteTaskId));
        log.info("The task with id = {} has been deleted", sampleDeleteTaskId);
    }

    @Test
    void deleteTask_DoesNotExist_ExceptionThrown() {
        long invalidId = 1000;

        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.deleteTask(invalidId),
                "Any tasks that can't be found should throw an error.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void deleteTask_DeletedTask_ExceptionThrown() {
        long alreadyDeletedId = 1;
        Exception e = assertThrows(TaskAlreadyDeletedException.class,
                () -> scrumBoardService.deleteTask(alreadyDeletedId),
                "Any tasks that is already deleted and tries to be deleted again should throw an error.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask() {
        long sampleUpdateId = 16;
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO("Updated Task", "Updated Description", "DONE");
        Task sampleUpdateTaskResponse = scrumBoardService.updateTask(sampleUpdateId, sampleUpdateTask);

        assertNotNull(sampleUpdateId);
        log.info("Update task with id = {}. The new updated task now contains: {}",
                sampleUpdateId, gson.toJson(sampleUpdateTaskResponse));
    }

    @Test
    void updateTask_InvalidTaskStatus_ExceptionThrown() {
        long taskIdToUpdate = 16;
        TaskRequestDTO taskWithInvalidStatus = new TaskRequestDTO(
                "Something", "Something here", "INVALID STATUS");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.updateTask(taskIdToUpdate, taskWithInvalidStatus),
                "Invalid status inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_InvalidTitle_ExceptionThrown() {
        long taskIdToUpdate = 16;
        TaskRequestDTO taskWithInvalidTitle = new TaskRequestDTO(null, "Something", "TODO");

        Exception e = assertThrows(InvalidTaskRequestException.class,
                () -> scrumBoardService.updateTask(taskIdToUpdate, taskWithInvalidTitle),
                "Invalid title inputs should throw");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_DeletedTask_ExceptionThrown() {
        long taskIdThatIsDeleted = 1;
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO(
                "Updated Task", "Updated Description", "DONE");

        Exception e = assertThrows(TaskAlreadyDeletedException.class,
                () -> scrumBoardService.updateTask(taskIdThatIsDeleted, sampleUpdateTask),
                "Any updates to a deleted task should be thrown.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    void updateTask_DoesNotExist_ExceptionThrown() {
        long taskIdThatDoesNotExist = 1000;
        TaskRequestDTO sampleUpdateTask = new TaskRequestDTO(
                "Updated Task", "Updated Description", "DONE");

        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.updateTask(taskIdThatDoesNotExist, sampleUpdateTask),
                "Any invalid task id should throw.");

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }

    @Test
    @Disabled
    void sampleTest() {
        // Create Task 1
        TaskRequestDTO createTask1 = new TaskRequestDTO("Complete Test 1", "Complete Desc 1", "TODO");
        Task taskCreated1 = scrumBoardService.createTask(createTask1);

        assertNotNull(taskCreated1);
        log.info("The task was created with the following information: {}", gson.toJson(taskCreated1));

        // Create Task 2
        TaskRequestDTO createTask2 = new TaskRequestDTO("Complete Test 2", "Complete Desc 2", "TODO");
        Task taskCreated2 = scrumBoardService.createTask(createTask2);

        assertNotNull(taskCreated2);
        log.info("The task was created with the following information: {}", gson.toJson(taskCreated2));

        assertNotEquals(taskCreated1, taskCreated2);

        // Update Task 1
        long updateTaskId = 16;
        TaskRequestDTO updateTaskRequest = new TaskRequestDTO("Updated Task 1", "Updated Description 1", "DONE");
        Task updatedTask = scrumBoardService.updateTask(updateTaskId, updateTaskRequest);

        assertNotNull(updatedTask);
        log.info("Update task with id = {}. The new updated task now contains: {}", updateTaskId, gson.toJson(updatedTask));

        // Delete Task 3 -- Task # 1 is the task that was created before this test.
        long deleteTaskId = 8;
        assertDoesNotThrow(() -> scrumBoardService.deleteTask(deleteTaskId));
        log.info("The task with id = {} has been deleted", deleteTaskId);

        // Get All Tasks (Both deleted and not deleted)
        List<Task> taskList = scrumBoardService.getAllTasks();
        log.info("The contents are: {}", gson.toJson(taskList));

        // Get ID of an existing task. Ex. 16
        long existingTaskId = 16;
        Task existingTask = scrumBoardService.getTaskById(existingTaskId);
        log.info("The task with id = {} has the following information: {}", existingTaskId, gson.toJson(existingTask));

        // Get ID that does not exist. Ex. 1000
        long nonExistingTask = 1000;
        Exception e = assertThrows(TaskNotFoundException.class,
                () -> scrumBoardService.getTaskById(nonExistingTask)
        );

        JsonElement je = JsonParser.parseString(e.getMessage());
        log.info("The error contains:\n{}", gson.toJson(je));
    }
}