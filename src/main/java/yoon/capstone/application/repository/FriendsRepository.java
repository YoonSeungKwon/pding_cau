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

    //Lazy Loading
    Optional<Friends> findFriendsByFriendIdx(long friendIdx);

    //Eagle Loading
    @Query("SELECT f FROM Friends f JOIN FETCH f.toUser WHERE f.friendIdx = :friendIndex")
    Optional<Friends> findFriendsByFriendIdxWithFetchJoin(@Param("friendIndex") long friendIndex);

    //Lazy Loading
    Optional<Friends> findFriendsByToUserAndFromUser(Members toUser, long fromUser);

    //Lazy Loading
    List<Friends> findAllByFromUser(long fromUser);

    //Duplication Check
    boolean existsByToUserAndFromUser(Members toUser, long fromUser);

    @Query("SELECT f FROM Friends f WHERE f.toUser.memberIdx = :memberIndex")
    List<Friends> findAllWithToUserIndex(@Param("memberIndex") long memberIndex);
}
