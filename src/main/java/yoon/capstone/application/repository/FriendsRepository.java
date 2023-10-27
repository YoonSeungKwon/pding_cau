package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Friends;
import yoon.capstone.application.domain.Members;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    Friends findFriendsByToUserAndFromUser(Members toUser, long fromUser);

    List<Friends> findAllByFromUser(long fromUser);

    boolean existsByToUserAndFromUser(Members toUser, long fromUser);
}
