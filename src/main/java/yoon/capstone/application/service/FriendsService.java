package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.common.dto.response.FriendsResponse;
import yoon.capstone.application.common.dto.response.MemberDetailResponse;
import yoon.capstone.application.common.dto.response.MemberResponse;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.common.util.EmailFormatManager;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.FriendsException;
import yoon.capstone.application.common.exception.UnauthorizedException;
import yoon.capstone.application.infrastructure.jpa.FriendsJpaRepository;
import yoon.capstone.application.infrastructure.jpa.MemberJpaRepository;
import yoon.capstone.application.config.security.JwtAuthentication;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final AesEncryptorManager aesEncryptorManager;

    private final MemberJpaRepository memberRepository;

    private final FriendsJpaRepository friendsRepository;

    private FriendsResponse toResponse(Friends friends){
        return new FriendsResponse(friends.getFriendIdx(), friends.getToUser().getUsername(), friends.isFriends(), friends.getCreatedAt());
    }

    private MemberResponse toMemberResponse(Members members){
        return new MemberResponse(members.getMemberIdx(), EmailFormatManager.toEmail(members.getEmail()), members.getUsername()
                , aesEncryptorManager.decode(members.getPhone()), members.getProfile(), members.isOauth(), members.getLastVisit());
    }

    private MemberDetailResponse toMemberDetailResponse(Members members){
        return new MemberDetailResponse(EmailFormatManager.toEmail(members.getEmail()), members.getUsername()
                , aesEncryptorManager.decode(members.getPhone()), members.getProfile(), members.isOauth(),members.getCreatedAt(), members.getLastVisit());
    }

    // 친구 목록, 친구 요청, 친구 수락, 친구 거절, 친구 삭제, 친구 페이지, 등..
    @Transactional(readOnly = true)
    public List<MemberResponse> getFriendsList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication dto = (JwtAuthentication) authentication.getPrincipal();

        //Eagle Loading
        return friendsRepository.findAllByFromUserWithFetchJoin(
                dto.getMemberIdx()).stream().map((friends)->toMemberResponse(friends.getToUser())).toList();
    }

    @Transactional(readOnly = true)
    public List<FriendsReqResponse> getFriendsRequest(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication dto = (JwtAuthentication) authentication.getPrincipal();

        return friendsRepository.findAllRequestsByToUser(dto.getMemberIdx());
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse friendsDetail(long memberIndex){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Lazy Loading
        Members members = memberRepository.findMembersByMemberIdxAndIsFriend(memberIndex, memberDto.getMemberIdx()).orElseThrow(()->new UsernameNotFoundException(null));

        return toMemberDetailResponse(members);
    }

    @Transactional
    public FriendsResponse requestFriends(long memberIndex){ //친구 요청
        //Lazy Loading
        Members toUser = memberRepository.findMembersByMemberIdx(memberIndex).orElseThrow(()->new UsernameNotFoundException(null));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        //DTO
        JwtAuthentication fromUser = (JwtAuthentication) authentication.getPrincipal();

        if(toUser.getMemberIdx() == fromUser.getMemberIdx())
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

    @Transactional
    public void declineFriends(long friendIdx) { //친구 요청 거절

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Lazy Loading
        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(() -> new FriendsException(ExceptionCode.NOT_FRIENDS));
        if(!memberDto.getEmail().equals(friends.getToUser().getEmail())) throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS);

        friendsRepository.delete(friends);
    }

    @Transactional
    public List<FriendsResponse> acceptFriends(long friendIdx){  //친구 요청 수락

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Lazy Loading
        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx()).orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        //Lazy Loading
        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        //Lazy Loading
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

        List<Friends> list = friendsRepository.findAllByFromUserWithFetchJoin(currentMember.getMemberIdx());
        return list.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteFriends(long friendIdx){  //친구 목록 삭제
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx())
                .orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

        //Lazy Loading
        Friends friends = friendsRepository.findFriendsByFriendIdx(friendIdx).orElseThrow(()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        friendsRepository.delete(friends);

        //Lazy Loading
        Friends tempFriends = friendsRepository.findFriendsByToUserAndFromUserAndFriends(friends.getToUser(), currentMember.getMemberIdx(), true).orElseThrow(
                ()->new FriendsException(ExceptionCode.NOT_FRIENDS));

        friendsRepository.delete(tempFriends);

    }

}
