package com.pagejump.scrumboardwebclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class ScrumBoardController {

    @GetMapping
    public Mono<String> greet() {
        Mono<String> test = Mono.just("Hello World.");
        return test;
    }
}
