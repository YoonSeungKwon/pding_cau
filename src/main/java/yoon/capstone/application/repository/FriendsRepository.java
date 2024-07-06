package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    Friends findFriendsByToUserAndFromUser(Members toUser, long fromUser);

    List<Friends> findAllByFromUser(long fromUser);

    boolean existsByToUserAndFromUser(Members toUser, long fromUser);

    List<Friends> findAllByToUser(Members toUser);
}
