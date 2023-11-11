package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;
import yoon.capstone.application.service.ProjectService;
import yoon.capstone.application.vo.response.ProjectDetailResponse;
import yoon.capstone.application.vo.response.ProjectResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{email}/{oauth}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> getList(@PathVariable String email, @PathVariable String oauth){
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    public ResponseEntity<ProjectDetailResponse> getList(@PathVariable String email, @PathVariable String oauth, @PathVariable String idx){
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
