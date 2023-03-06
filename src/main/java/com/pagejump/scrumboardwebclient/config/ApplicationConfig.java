package com.pagejump.scrumboardwebclient.config;

import com.pagejump.scrumboardwebclient.dto.TaskRequestDTO;
import com.pagejump.scrumboardwebclient.model.Task;
import com.pagejump.scrumboardwebclient.service.ScrumBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class ApplicationConfig {

    @Bean
    CommandLineRunner commandLineRunner(ScrumBoardService scrumBoardService) {
        return args -> {
            // Getting all tasks.
//            List<Task> taskList = scrumBoardService.getAllTasks();
//            for (Task task : taskList) {
//                log.info("The list has task: {}", task);
//            }

            // Getting task by Id.
//            Task taskGottenById = scrumBoardService.getTaskById(10);
//            log.info("You've got the task with id = {} with the ff. information: {}", taskGottenById.getId(), taskGottenById);

            // Deleting a task.
//            long DELETE_ID = 10;
//            scrumBoardService.deleteTask(DELETE_ID);
//            log.info("I've deleted the task with id = {}", DELETE_ID);

            // Creating a task.

            // For create, create TaskRequestDTO.
//            TaskRequestDTO createRequest = new TaskRequestDTO(
//                    "Creating a second task using Spring WebClient",
//                    "Passing the value as an object.",
//                    "TODO"
//            );
//
//            Task gottenFromCreate = scrumBoardService.createTask(createRequest);
//            log.info("I've create the task with the ff. info: {}", gottenFromCreate.toString());

            // For updating an existing task.
//            long UPDATE_ID = 12;
//            TaskRequestDTO updateRequest = new TaskRequestDTO(
//                    "Updating Task # 12",
//                    "I've added too many of the same tasks by mistake",
//                    "DONE"
//            );
//
//            Task gottenFromUpdate = scrumBoardService.updateTask(UPDATE_ID, updateRequest);
//            log.info("I've create the task with the ff. info: {}", gottenFromUpdate.toString());
        };
    }
}
