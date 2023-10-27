package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.vo.request.FriendsDto;
import yoon.capstone.application.vo.response.FriendsReponse;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final MemberRepository memberRepository;

    private FriendsReponse toResponse(Friends friends){
        Members fromUser = memberRepository.findMembersByIdx(friends.getFromUser());
        return new FriendsReponse(friends.getToUser().getUsername(), fromUser.getUsername(), friends.isFriends());
    }

    // 친구 요청, 친구 수락, 친구 거절, 친구 삭제, 친구 페이지, 등..

    public FriendsReponse requestFriends(FriendsDto dto){ //친구 요청
        Members members = memberRepository.findMembersByEmail(dto.getToUserEmail());
        if(members == null)
            throw new UsernameNotFoundException(dto.getToUserEmail());

        Members fromUser = memberRepository.findMembersByEmail(dto.getFromUserEmail());

        Friends friends = Friends.builder()
                .toUser(members)
                .fromUser(fromUser)
                .build();

        //소켓 통신으로 친구 요청 알림 보내기

        return toResponse(friendsRepository.save(friends));
    }

    public FriendsReponse acceptFriends(FriendsDto dto){  //친구 요청 수락
        Members toUser = memberRepository.findMembersByEmail(dto.getToUserEmail());
        Members fromUser = memberRepository.findMembersByEmail(dto.getFromUserEmail());

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(toUser, fromUser.getIdx());

        friends.setFriends(true);

        return toResponse(friendsRepository.save(friends));
    }

    public FriendsReponse declineFriends(FriendsDto dto){ //친구 요청 거절
        Members toUser = memberRepository.findMembersByEmail(dto.getToUserEmail());
        Members fromUser = memberRepository.findMembersByEmail(dto.getFromUserEmail());

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(toUser, fromUser.getIdx());

        friendsRepository.delete(friends);

        return toResponse(friends);
    }

    public FriendsReponse deleteFriends(String email){  //친구 목록 삭제
        Members members = memberRepository.findMembersByEmail(email);
        if(members == null)
            throw new UsernameNotFoundException(email);

        Members me = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Friends friends = friendsRepository.findFriendsByToUserAndFromUser(members, me.getIdx());

        friendsRepository.delete(friends);

        return toResponse(friends);
    }

}
