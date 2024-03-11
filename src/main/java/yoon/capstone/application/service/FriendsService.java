package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.vo.request.FriendsDto;
import yoon.capstone.application.vo.response.FriendsReqResponse;
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
        return new FriendsResponse(friends.getToUser().getUsername(), fromUser.getUsername(), friends.isFriends(), friends.getRegdate());
    }

    // 친구 목록, 친구 요청, 친구 수락, 친구 거절, 친구 삭제, 친구 페이지, 등..
    @Cacheable(value = "friendsList", key = "#email")
    public List<MemberResponse> getFriendsList(String email){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Friends> list = friendsRepository.findAllByFromUser(members.getIdx());
        List<MemberResponse> result = new ArrayList<>();

        for(Friends f: list){
            if(f.isFriends())
                result.add(new MemberResponse(f.getToUser().getEmail(), f.getToUser().getUsername(), f.getToUser().getProfile()
                , f.getToUser().isOauth(), f.getToUser().getLastVisit()));
        }

        return result;
    }

    public List<FriendsReqResponse> getFriendsRequest(){
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Friends> list = friendsRepository.findAllByToUser(members);
        List<FriendsReqResponse> result = new ArrayList<>();

        for(Friends f:list){
            if(!f.isFriends()) {
                Members tempMember = memberRepository.findMembersByIdx(f.getFromUser());
                result.add(new FriendsReqResponse(tempMember.getEmail(), tempMember.getUsername(), tempMember.getProfile(), tempMember.isOauth(),
                        f.getRegdate()));
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
        if(members.equals(fromUser))
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

    @CachePut(value = "friendsList", key = "#email")
    public List<FriendsResponse> acceptFriends(FriendsDto dto, String email){  //친구 요청 수락
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
        friendsRepository.save(friends);

        List<Friends> list = friendsRepository.findAllByToUser(toUser);
        List<FriendsResponse> result = new ArrayList<>();
        for(Friends f : list){
            result.add(toResponse(f));
        }
        return result;
    }

    public List<FriendsResponse> declineFriends(FriendsDto dto){ //친구 요청 거절
        Members toUser = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Members fromUser = memberRepository.findMembersByEmailAndOauth(dto.getFromUserEmail(), dto.isOauth());

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(toUser, fromUser.getIdx());
        if(friends == null)
            throw new FriendsException(ErrorCode.NOT_FRIENDS.getStatus());
        friendsRepository.delete(friends);

        List<Friends> list = friendsRepository.findAllByToUser(toUser);
        List<FriendsResponse> result = new ArrayList<>();
        for(Friends f : list){
            result.add(toResponse(f));
        }


        return result;
    }

    @CachePut(value = "friendsList", key = "#email")
    public List<FriendsResponse> deleteFriends(FriendsDto dto, String email){  //친구 목록 삭제
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

        List<Friends> list = friendsRepository.findAllByFromUser(me.getIdx());
        List<FriendsResponse> result = new ArrayList<>();
        for(Friends f : list){
            result.add(toResponse(f));
        }

        return result;
    }

}
