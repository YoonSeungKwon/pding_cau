package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    Friends findFriendsByToUserAndFromUser(Members toUser, long fromUser);

}
