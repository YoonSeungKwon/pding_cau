package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.dto.request.FriendsDto;
import yoon.capstone.application.dto.response.FriendsReqResponse;
import yoon.capstone.application.dto.response.FriendsResponse;
import yoon.capstone.application.dto.response.MemberDetailResponse;
import yoon.capstone.application.dto.response.MemberResponse;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.FriendsException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.security.JwtAuthentication;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final AesBytesEncryptor aesBytesEncryptor;

    private final MemberRepository memberRepository;

    private final FriendsRepository friendsRepository;

    private FriendsResponse toResponse(Friends friends){
        Members fromUser = memberRepository.findMembersByMemberIdx(friends.getFromUser()).orElseThrow(()->new UsernameNotFoundException(null));
        return new FriendsResponse(friends.getFriendIdx(), friends.getToUser().getUsername(), fromUser.getUsername(), friends.isFriends(), friends.getCreatedAt());
    }

    private MemberResponse toMemberResponse(Members members){
        byte[] bytePhone = Base64.getDecoder().decode(members.getPhone());
        String phone = new String(aesBytesEncryptor.decrypt(bytePhone), StandardCharsets.UTF_8);
        return new MemberResponse(members.getEmail(), members.getUsername(), phone, members.getProfile(), members.isOauth(), members.getLastVisit());
    }

    private MemberDetailResponse toMemberDetailResponse(Members members){
        byte[] bytePhone = Base64.getDecoder().decode(members.getPhone());
        String phone = new String(aesBytesEncryptor.decrypt(bytePhone), StandardCharsets.UTF_8);
        return new MemberDetailResponse(members.getEmail(), members.getUsername(), phone, members.getProfile(), members.isOauth()
                ,members.getCreatedAt(), members.getLastVisit());
    }

    // 친구 목록, 친구 요청, 친구 수락, 친구 거절, 친구 삭제, 친구 페이지, 등..
    @Cacheable(value = "friendsList", key = "#cacheIndex")
    public List<MemberResponse> getFriendsList(long cacheIndex){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication dto = (JwtAuthentication) authentication.getPrincipal();

        return memberRepository.findAllWithFromUser(
                dto.getMemberIdx()).stream().map(this::toMemberResponse).toList();
    }

    public List<FriendsReqResponse> getFriendsRequest(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication dto = (JwtAuthentication) authentication.getPrincipal();

        List<Friends> list = friendsRepository.findAllWithToUserIndex(dto.getMemberIdx());
        List<FriendsReqResponse> result = new ArrayList<>();

        for(Friends f:list){
            if(!f.isFriends()) {
                Members tempMember = memberRepository.findMembersByMemberIdx(f.getFromUser()).orElseThrow(()->new UsernameNotFoundException(null));
                result.add(new FriendsReqResponse(tempMember.getEmail(), tempMember.getUsername(), tempMember.getProfile(), tempMember.isOauth(),
                        f.getCreatedAt()));
            }
        }

        return result;
    }

    public MemberDetailResponse friendsDetail(FriendsDto dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        Members members = memberRepository.findMembersByEmail(dto.getToUserEmail()).orElseThrow(()->new UsernameNotFoundException(dto.getToUserEmail()));
        if(!friendsRepository.existsByToUserAndFromUser(members, memberDto.getMemberIdx()))
            throw new FriendsException(ExceptionCode.NOT_FRIENDS);
        return toMemberDetailResponse(members);
    }

    @Transactional
    public FriendsResponse requestFriends(FriendsDto dto){ //친구 요청
        Members toUser = memberRepository.findMembersByEmail(dto.getToUserEmail()).orElseThrow(()->new UsernameNotFoundException(dto.getToUserEmail()));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication fromUser = (JwtAuthentication) authentication.getPrincipal();

        if(toUser == null)
            throw new UsernameNotFoundException(dto.getToUserEmail());
        if(toUser.getMemberIdx() != fromUser.getMemberIdx())
            throw new FriendsException(ExceptionCode.SELF_FRIENDS);
        if(friendsRepository.existsByToUserAndFromUser(toUser, fromUser.getMemberIdx()))
            throw new FriendsException(ExceptionCode.ALREADY_FRIENDS);        // 이미 친구로 등록되어 있거나 친구 요청을 보냄

        Friends friends = Friends.builder()
                .toUser(toUser)
                .fromUser(fromUser.getMemberIdx())
                .build();

        //소켓 통신으로 친구 요청 알림 보내기

        return toResponse(friendsRepository.save(friends));
    }

    public void declineFriends(long friendIdx) { //친구 요청 거절

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(() -> new FriendsException(ExceptionCode.NOT_FRIENDS));
        if(!memberDto.getEmail().equals(friends.getToUser().getEmail())) throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS);

        friendsRepository.delete(friends);
    }

    @CachePut(value = "friendsList", key = "#cacheIndex")
    @Transactional
    public List<FriendsResponse> acceptFriends(long friendIdx, long cacheIndex){  //친구 요청 수락

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();
        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx()).orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        Members fromUser = memberRepository.findMembersByMemberIdx(friends.getFromUser()).orElseThrow(()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        if(friends.isFriends())
            throw new FriendsException(ExceptionCode.ALREADY_FRIENDS);

        friends.setFriends(true);

        Friends temp = Friends.builder()
                .fromUser(currentMember.getMemberIdx())
                .toUser(fromUser)
                .build();
        temp.setFriends(true);

        friendsRepository.save(temp);   //수락한 쪽도 친구로 등록
        friendsRepository.save(friends);

        List<Friends> list = friendsRepository.findAllByFromUser(currentMember.getMemberIdx());
        List<FriendsResponse> result = new ArrayList<>();
        for(Friends f : list){
            result.add(toResponse(f));
        }
        return result;
    }

    @CachePut(value = "friendsList", key = "#cacheIndex")
    @Transactional
    public void deleteFriends(long friendIdx, long cacheIndex){  //친구 목록 삭제
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();
        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx()).orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        friendsRepository.delete(friends);

        Friends tempFriends = friendsRepository.findFriendsByToUserAndFromUser(friends.getToUser(), currentMember.getMemberIdx()).orElseThrow(
                ()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        friendsRepository.delete(tempFriends);

    }

}
