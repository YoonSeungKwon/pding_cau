package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.service.FriendsService;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.vo.request.FriendsDto;
import yoon.capstone.application.vo.response.FriendsResponse;
import yoon.capstone.application.vo.response.MemberDetailResponse;
import yoon.capstone.application.vo.response.MemberResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/")
    public ResponseEntity<List<MemberResponse>> getRequests(){

        List<MemberResponse> result = friendsService.getFriendsRequest();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/info/{email}")                        //친구 정보, 상태 보기
    public ResponseEntity<MemberDetailResponse> getFriends(@PathVariable String email){

        MemberDetailResponse result = friendsService.friendsDetail(email);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }//친구만 정보를 볼 수 있게 수정 필요!

    @GetMapping("/lists")                      //친구 목록 불러오기
    public ResponseEntity<List<MemberResponse>> getFriendsList(){

        List<MemberResponse> result = friendsService.getFriendsList();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/link")                       //친구 요청 보내기
    public ResponseEntity<FriendsResponse> requestFriends(@RequestBody FriendsDto dto){

        FriendsResponse result = friendsService.requestFriends(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/answer/{status}")                     //친구 요청 응답
    public ResponseEntity<FriendsResponse> responseFriends(@PathVariable String status, @RequestBody FriendsDto dto){

        FriendsResponse result;

        if(status.equals("ok")){
            result = friendsService.acceptFriends(dto);
        }else{
            result = friendsService.declineFriends(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/unlink/{email}")             //친구 삭제
    public ResponseEntity<FriendsResponse> deleteFriends(@PathVariable String email){

        FriendsResponse result = friendsService.deleteFriends(email);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }



}
