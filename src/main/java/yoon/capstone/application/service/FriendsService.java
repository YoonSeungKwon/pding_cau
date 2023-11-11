package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.vo.request.FriendsDto;
import yoon.capstone.application.vo.response.FriendsResponse;
import yoon.capstone.application.vo.response.MemberDetailResponse;
import yoon.capstone.application.vo.response.MemberResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final MemberRepository memberRepository;

    private FriendsResponse toResponse(Friends friends){
        Members fromUser = memberRepository.findMembersByIdx(friends.getFromUser());
        return new FriendsResponse(friends.getToUser().getUsername(), fromUser.getUsername(), friends.isFriends());
    }

    // 친구 목록, 친구 요청, 친구 수락, 친구 거절, 친구 삭제, 친구 페이지, 등..

    public List<MemberResponse> getFriendsList(){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Friends> list = friendsRepository.findAllByFromUser(members.getIdx());
        List<MemberResponse> result = new ArrayList<>();

        for(Friends f: list){
            if(f.isFriends())
                result.add(new MemberResponse(f.getToUser().getEmail(), f.getToUser().getUsername(), f.getToUser().getProfile()
                , f.getToUser().isOauth()));
        }

        return result;
    }

    public List<MemberResponse> getFriendsRequest(){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Friends> list = friendsRepository.findAllByToUser(members);
        List<MemberResponse> result = new ArrayList<>();

        for(Friends f:list){
            if(!f.isFriends()) {
                Members tempMember = memberRepository.findMembersByIdx(f.getFromUser());
                result.add(new MemberResponse(tempMember.getEmail(), tempMember.getUsername(), tempMember.getProfile(), tempMember.isOauth()));
            }
        }

        return result;
    }

    public MemberDetailResponse friendsDetail(FriendsDto dto){
        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members members = memberRepository.findMembersByEmailAndOauth(dto.getToUserEmail(), dto.isOauth());
        if(!friendsRepository.existsByToUserAndFromUser(me, members.getIdx()))
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());
        return new MemberDetailResponse(members.getEmail(), members.getUsername(), members.getProfile(), members.isOauth(),
                members.getRegdate(), members.getLastVisit(), members.getPhone());
    }

    public FriendsResponse requestFriends(FriendsDto dto){ //친구 요청
        Members members = memberRepository.findMembersByEmailAndOauth(dto.getToUserEmail(), dto.isOauth());
        Members fromUser = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(members == null)
            throw new UsernameNotFoundException(dto.getToUserEmail());
        if(members == fromUser)
            throw new FriendsException(ErrorCode.SELF_FRIENDS.getStatus());
        if(friendsRepository.existsByToUserAndFromUser(members, fromUser.getIdx()))
            throw new FriendsException(ErrorCode.ALREADY_FRIENDS.getStatus());        // 이미 친구로 등록되어 있거나 친구 요청을 보냄

        Friends friends = Friends.builder()
                .toUser(members)
                .fromUser(fromUser)
                .build();

        //소켓 통신으로 친구 요청 알림 보내기

        return toResponse(friendsRepository.save(friends));
    }

    public FriendsResponse acceptFriends(FriendsDto dto){  //친구 요청 수락
        Members toUser = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members fromUser = memberRepository.findMembersByEmailAndOauth(dto.getFromUserEmail(), dto.isOauth());

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(toUser, fromUser.getIdx());
        if(friends == null)
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());
        if(friends.isFriends())
            throw new FriendsException(ErrorCode.ALREADY_FRIENDS.getStatus());

        friends.setFriends(true);

        Friends temp = Friends.builder()
                .fromUser(toUser)
                .toUser(fromUser)
                .build();
        temp.setFriends(true);

        friendsRepository.save(temp);   //수락한 쪽도 친구로 등록
        return toResponse(friendsRepository.save(friends));
    }

    public FriendsResponse declineFriends(FriendsDto dto){ //친구 요청 거절
        Members toUser = (Members) SecurityContextHolder.getContext().getAuthentication();
        Members fromUser = memberRepository.findMembersByEmailAndOauth(dto.getFromUserEmail(), dto.isOauth());

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(toUser, fromUser.getIdx());
        if(friends == null)
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());
        friendsRepository.delete(friends);

        return toResponse(friends);
    }

    public FriendsResponse deleteFriends(FriendsDto dto){  //친구 목록 삭제
        Members members = memberRepository.findMembersByEmailAndOauth(dto.getToUserEmail(), dto.isOauth());
        if(members == null)
            throw new UsernameNotFoundException(dto.getToUserEmail());

        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(members, me.getIdx());
        if(friends == null)
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());
        friendsRepository.delete(friends);

        Friends tempFriends = friendsRepository.findFriendsByToUserAndFromUser(me, members.getIdx());
        friendsRepository.delete(tempFriends);

        return toResponse(friends);
    }

}
