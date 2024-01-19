package yoon.capstone.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "펀딩 관련 API", description = "v1")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/")
    @Operation(summary = "본인의 펀딩 글 불러오기", description = "본인이 작성한 펀딩 글을 불러온다.")
    public ResponseEntity<List<ProjectResponse>> getList() {

        List<ProjectResponse> result = projectService.getProjectList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/friends")
    @Operation(summary = "친구들의 펀딩 글 불러오기", description = "친구로 등록된 유저들의 펀딩 글을 불러온다.")
    public ResponseEntity<List<ProjectResponse>> getFriendsList() {

        List<ProjectResponse> result = projectService.getFriendsList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    @Operation(summary = "펀딩 글 자세히 보기", description = "idx에 해당하는 프로젝트의 자세한 정보들을 반환")
    public ResponseEntity<ProjectDetailResponse> getList(@PathVariable long idx) {

        ProjectDetailResponse result = projectService.getProjectDetail(idx);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(summary = "펀딩 글 쓰기", description = "지정된 형식의 dto와 파일을 받아서 유효성 검사 후 펀딩 글 등록")
    public ResponseEntity<ProjectResponse> makeProject(@RequestPart MultipartFile file, @RequestPart @Validated(ProjectValidationSequence.class) ProjectDto dto) {

        ProjectResponse result = projectService.makeProjects(file, dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{idx}")
    @Operation(summary = "펀딩 글 대표 이미지 변경", description = "file을 유효성을 검증한 후 스토리지 서버에 저장")
    public ResponseEntity<String> changeProjectImage(@PathVariable long idx, @RequestBody MultipartFile file) {
        String url = projectService.changeImage(idx, file);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
