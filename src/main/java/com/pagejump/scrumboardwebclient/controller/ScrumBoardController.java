package com.pagejump.scrumboardwebclient.controller;

import com.pagejump.scrumboardwebclient.model.Task;
import com.pagejump.scrumboardwebclient.service.ScrumBoardService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ScrumBoardController {
    private final ScrumBoardService scrumBoardService;

    @GetMapping
    public Mono<String> getAllTasks() {
        Mono<String> test = Mono.just("Hello World! I'm just here for display. All the real stuff are on" +
                " the command line.");
        return test;
    }
}
