package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import yoon.capstone.application.service.ProjectService;
import yoon.capstone.application.vo.request.ProjectDto;
import yoon.capstone.application.vo.response.ProjectDetailResponse;
import yoon.capstone.application.vo.response.ProjectResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{email}/social={oauth}/projects")
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

    @PostMapping("/")
    public ResponseEntity<ProjectResponse> makeProject(@PathVariable String email, @PathVariable String oauth,
                                                             @RequestBody ProjectDto dto){

        ProjectResponse response = projectService.makeProjects(dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
