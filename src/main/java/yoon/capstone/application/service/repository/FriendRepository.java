package yoon.capstone.application.service.repository;

import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;

import java.util.List;
import java.util.Optional;

public interface FriendRepository {

    Optional<Friends> findFriend(long index);

    Optional<Friends> findFriend(Members toUser, long fromUser, boolean isFriend);

    List<Friends> findAllFriend(long fromUser);

    boolean checkFriend(Members toUser, long fromUser);

    List<FriendsReqResponse> findAllRequest(long toUser);

    Friends save(Friends friends);

    void delete(Friends friends);

}
