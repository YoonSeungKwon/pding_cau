package yoon.capstone.application.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.infra.jpa.FriendsJpaRepository;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.repository.FriendRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {

    private final FriendsJpaRepository friendsJpaRepository;


    @Override
    public Optional<Friends> findFriend(long index) {
        return friendsJpaRepository.findFriendsByFriendIdx(index);
    }

    @Override
    public Optional<Friends> findFriend(Members toUser, long fromUser, boolean isFriend) {
        return friendsJpaRepository.findFriendsByToUserAndFromUserAndFriends(toUser, fromUser, isFriend);
    }

    @Override
    public List<Friends> findAllFriend(long fromUser) {
        return friendsJpaRepository.findAllByFromUserWithFetchJoin(fromUser);
    }

    @Override
    public boolean checkFriend(Members toUser, long fromUser) {
        return friendsJpaRepository.existsByToUserAndFromUser(toUser, fromUser);
    }

    @Override
    public List<FriendsReqResponse> findAllRequest(long toUser) {
        return friendsJpaRepository.findAllRequestsByToUser(toUser);
    }

    @Override
    public Friends save(Friends friends) {
        return friendsJpaRepository.save(friends);
    }

    @Override
    public void delete(Friends friends) {
        friendsJpaRepository.delete(friends);
    }
}
