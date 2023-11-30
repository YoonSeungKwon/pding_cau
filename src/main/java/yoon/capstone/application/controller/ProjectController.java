package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.GetExchange;
import yoon.capstone.application.exception.sequence.ProjectValidationSequence;
import yoon.capstone.application.service.ProjectService;
import yoon.capstone.application.vo.request.ProjectDto;
import yoon.capstone.application.vo.response.ProjectDetailResponse;
import yoon.capstone.application.vo.response.ProjectResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> getList() {

        List<ProjectResponse> result = projectService.getProjectList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<ProjectResponse>> getFriendsList() {

        List<ProjectResponse> result = projectService.getFriendsList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    public ResponseEntity<ProjectDetailResponse> getList(@PathVariable long idx) {

        ProjectDetailResponse result = projectService.getProjectDetail(idx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ProjectResponse> makeProject(@RequestPart MultipartFile file, @RequestPart @Validated(ProjectValidationSequence.class) ProjectDto dto) {

        ProjectResponse result = projectService.makeProjects(file, dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{idx}")
    public ResponseEntity<String> changeProjectImage(@PathVariable long idx, @RequestBody MultipartFile file) {
        String url = projectService.changeImage(idx, file);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
