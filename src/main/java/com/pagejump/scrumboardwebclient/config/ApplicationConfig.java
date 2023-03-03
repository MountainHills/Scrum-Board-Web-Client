package com.pagejump.scrumboardwebclient.config;

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
            List<Task> taskList = scrumBoardService.getAllTasks();
            for (Task task : taskList) {
                log.info("The list has task: {}", task);
            }

            Task taskGottenById = scrumBoardService.getTaskById(2);
            log.info("You've got the task with id = {} with the ff. information: {}", taskGottenById.getId(), taskGottenById);


        };
    }
}
