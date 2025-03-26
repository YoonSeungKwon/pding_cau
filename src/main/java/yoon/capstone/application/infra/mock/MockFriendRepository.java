package yoon.capstone.application.infra.mock;

import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;
import yoon.capstone.application.service.repository.FriendRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockFriendRepository implements FriendRepository {

    private int index = 0;
    private List<Friends> list = new ArrayList<>();

    @Override
    public Optional<Friends> findFriend(long index) {
        for(Friends f: list){
            if(f.getFriendIdx() == index)return Optional.of(f);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Friends> findFriend(Members toUser, long fromUser, boolean isFriend) {
        return Optional.of(Friends.builder().fromUser(fromUser).toUser(toUser).build());
    }

    @Override
    public List<Friends> findAllFriend(long fromUser) {
        List<Friends> result = new ArrayList<>();
        for(Friends f : list){
            if(f.getFromUser() == fromUser)result.add(f);
        }
        return result;
    }

    @Override
    public boolean checkFriend(Members toUser, long fromUser) {
        for(Friends friends : list){
            if(friends.getToUser().getMemberIdx() == toUser.getMemberIdx() && friends.getFromUser() == fromUser)
                return true;
        }
        return false;
    }

    @Override
    public List<FriendsReqResponse> findAllRequest(long toUser) {
        List<FriendsReqResponse> result = new ArrayList<>();
        for(Friends f : list){
            if(f.getToUser().getMemberIdx() == toUser)
                result.add(new FriendsReqResponse(f.getFriendIdx(), "test"+f.getFromUser()+"@test.com", "tester"+f.getFromUser()
                , null, null));
        }
        return result;
    }

    @Override
    public Friends save(Friends friends) {
        boolean exist = false;
        for(Friends f: list){
            if(f.getFromUser() == friends.getFromUser() && f.getToUser().getMemberIdx() == friends.getToUser().getMemberIdx()){
                friends.setFriendIdx(f.getFriendIdx());
                list.remove(f);
                list.add(friends);
                exist = true;
                break;
            }
        }

        if(!exist){
            friends.setFriendIdx(index);
            index++;
            list.add(friends);
        }
        return friends;
    }

    @Override
    public void delete(Friends friends) {
        list.remove(friends);
    }
}
