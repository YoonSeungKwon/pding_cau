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
@RequestMapping("/api/v1/{email}/social={oauth}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> getList(@PathVariable String email, @PathVariable boolean oauth){

        List<ProjectResponse> result = projectService.getProjectList(email, oauth);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    public ResponseEntity<ProjectDetailResponse> getList(@PathVariable String email, @PathVariable boolean oauth, @PathVariable long idx){

        ProjectDetailResponse result = projectService.getProjectDetail(email, oauth, idx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<ProjectResponse> makeProject(@PathVariable String email, @PathVariable boolean oauth,
                                                       @RequestPart MultipartFile file, @RequestPart @Validated(ProjectValidationSequence.class) ProjectDto dto){

        ProjectResponse result = projectService.makeProjects(email, oauth, file, dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
