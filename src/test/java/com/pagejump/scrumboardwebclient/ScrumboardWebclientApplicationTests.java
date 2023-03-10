package com.pagejump.scrumboardwebclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.exception.InvalidTaskRequestException;
import com.pagejump.scrumboardwebclient.exception.TaskNotFoundException;
import com.pagejump.scrumboardwebclient.model.Task;
import com.pagejump.scrumboardwebclient.service.ScrumBoardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
class ScrumboardWebclientApplicationTests {

	private final String DOES_NOT_EXIST_ID = "21ce319b-0eae-41d5-b25a-016122d5798b"; // Random UUID. It doesn't exist.
	private final String DOES_EXIST_ID = "3ac096ec-1282-4e76-b1b0-4923a5aa05eb";
	private final String DELETE_ID = "afb67416-8fd6-4484-8bb6-94be533cfe66";
	private final String UPDATE_ID = "87dc9fa0-af6f-4078-b084-31de854056bc";
	private final String ALREADY_DELETED_ID = "e9dd1432-d788-4553-a56f-3783fc0e9371";

	@Autowired
	ScrumBoardService scrumBoardService;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Test
	void contextLoads() {
		System.out.println(scrumBoardService.getAllTasks().toString());
	}

	@Test
	void getTaskById_DoesExists_Task() {
		Task sampleTask = scrumBoardService.getTaskByIdErrorMap(DOES_NOT_EXIST_ID);

//		Exception e = assertThrows(TaskNotFoundException.class,
//				() -> scrumBoardService.getTaskById(DOES_NOT_EXIST_ID),
//				"Invalid ids should throw");

		assertNotNull(sampleTask);
		log.info("The task with id = {} has the following information: {}", DOES_EXIST_ID, gson.toJson(sampleTask));
	}

	@Test
	void createTask_ValidTaskRequest_Task() {
		TaskRequestDTO sampleCreateTask = new TaskRequestDTO(
				null, "Error Map Description", "TODO");
//		Task sampleCreatedTaskResponse = scrumBoardService.createTaskErrorMap(sampleCreateTask);

		Exception e = assertThrows(InvalidTaskRequestException.class,
				() -> scrumBoardService.createTaskErrorMap(sampleCreateTask),
				"Invalid title should throw");

		JsonElement je = JsonParser.parseString(e.getMessage());
		log.info("The error contains:\n{}", gson.toJson(je));

//		assertNotNull(sampleCreatedTaskResponse);
//		log.info("The task was created with the following information: {}", gson.toJson(sampleCreatedTaskResponse));
	}
}
