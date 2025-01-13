package yoon.capstone.application.infra.stub;

import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.repository.FriendRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StubFriendRepository implements FriendRepository {

    private List<Friends> list = new ArrayList<>();

    @Override
    public Optional<Friends> findFriend(long index) {
        return Optional.empty();
    }

    @Override
    public Optional<Friends> findFriend(Members toUser, long fromUser, boolean isFriend) {
        return Optional.of(Friends.builder().fromUser(fromUser).toUser(toUser).build());
    }

    @Override
    public List<Friends> findAllFriend(long fromUser) {
        return new ArrayList<>();
    }

    @Override
    public boolean checkFriend(Members toUser, long fromUser) {
        for(Friends friends : list){
            if(friends.getToUser() == toUser && friends.getFromUser() == fromUser)
                return friends.isFriends();
        }
        return false;
    }

    @Override
    public List<FriendsReqResponse> findAllRequest(long toUser) {
        return new ArrayList<>();
    }

    @Override
    public Friends save(Friends friends) {
        list.add(friends);
        return friends;
    }

    @Override
    public void delete(Friends friends) {
        list.remove(friends);
    }
}
