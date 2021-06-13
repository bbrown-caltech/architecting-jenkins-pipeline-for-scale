package com.simplilearn.cicd.project02;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DiagnosticsController {
    
    @GetMapping(value = "/healthz")
    public Mono<ResponseEntity<String>> healthz() {
        return Mono.just(ResponseEntity.ok().body("The test service is running."));
    }
}
