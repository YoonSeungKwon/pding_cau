package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Friends;
import yoon.capstone.application.entity.Members;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {

    Optional<Friends> findFriendsByFriendIdx(long friendIdx);
    Optional<Friends> findFriendsByToUserAndFromUser(Members toUser, long fromUser);
    List<Friends> findAllByFromUser(long fromUser);
    //Duplication Check
    boolean existsByToUserAndFromUser(Members toUser, long fromUser);
    @Query("SELECT f FROM Friends f WHERE f.toUser.memberIdx = :index")
    List<Friends> findAllWithToUserIndex(@Param("index") long idx);
}
