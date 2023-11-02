package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;
import yoon.capstone.application.service.ProjectService;

@RestController
@RequestMapping("/api/v1/{idx}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetExchange("/")
    public ResponseEntity<?> getList(@PathVariable String idx){
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
