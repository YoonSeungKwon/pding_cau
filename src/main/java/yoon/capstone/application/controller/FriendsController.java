package yoon.capstone.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.dto.request.FriendsDto;
import yoon.capstone.application.dto.response.FriendsReqResponse;
import yoon.capstone.application.dto.response.FriendsResponse;
import yoon.capstone.application.dto.response.MemberDetailResponse;
import yoon.capstone.application.dto.response.MemberResponse;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.security.JwtAuthentication;
import yoon.capstone.application.service.FriendsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
@Tag(name = "친구 관련 API", description = "v1")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/")
    @Operation(summary = "벋은 친구 요청 불러오기")
    public ResponseEntity<List<FriendsReqResponse>> getRequests(){

        List<FriendsReqResponse> result = friendsService.getFriendsRequest();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/info")                        //친구 정보, 상태 보기
    @Operation(summary = "친구 정보 불러오기")
    public ResponseEntity<MemberDetailResponse> getFriends(@RequestBody FriendsDto dto){

        MemberDetailResponse result = friendsService.friendsDetail(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }//친구만 정보를 볼 수 있게 수정

    @GetMapping("/lists")                      //친구 목록 불러오기
    @Operation(summary = "친구 목록 불러오기", description = "본인이 친구 목록을 가져온다. 신청중인 친구 제외")
    public ResponseEntity<List<MemberResponse>> getFriendsList(){

        List<MemberResponse> result = friendsService.getFriendsList(getCacheIndex());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/link")                       //친구 요청 보내기
    @Operation(summary = "친구 신청하기")
    public ResponseEntity<FriendsResponse> requestFriends(@RequestBody FriendsDto dto){

        FriendsResponse result = friendsService.requestFriends(dto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/answer/{status}")                     //친구 요청 응답
    @Operation(summary = "친구 요청 응답", description = "status에 따라서 친구요청 수락 혹은 거절 가능")
    public ResponseEntity<List<FriendsResponse>> responseFriends(@PathVariable String status, @RequestBody FriendsDto dto){

        List<FriendsResponse> result;

        if(status.equals("ok")){
            result = friendsService.acceptFriends(dto, getCacheIndex());
        }else{
            result = friendsService.declineFriends(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/unlink")             //친구 삭제
    @Operation(summary = "친구 삭제")
    public ResponseEntity<?> deleteFriends(@RequestBody FriendsDto dto){

        friendsService.deleteFriends(dto, getCacheIndex());

        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }


    private long getCacheIndex(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();
        return memberDto.getMemberIdx();
    }

}
