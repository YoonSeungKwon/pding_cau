package yoon.capstone.application.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.common.dto.response.FriendsReqResponse;
import yoon.capstone.application.service.domain.Friends;
import yoon.capstone.application.service.domain.Members;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsJpaRepository extends JpaRepository<Friends, Long> {

    //Lazy Loading
    Optional<Friends> findFriendsByFriendIdx(long friendIdx);

    //Lazy Loading
    @Query("SELECT f FROM Friends f WHERE f.isFriends = :friend AND f.toUser = :toUser AND f.fromUser = :fromUser")
    Optional<Friends> findFriendsByToUserAndFromUserAndFriends(@Param("toUser") Members toUser,@Param("fromUser") long fromUser,@Param("friend") boolean friend);

    //Eagle Loading
    @Query("SELECT f FROM Friends f JOIN FETCH f.toUser WHERE f.fromUser = :fromUser AND f.isFriends = true")
    List<Friends> findAllByFromUserWithFetchJoin(@Param("fromUser") long fromUser);

    //Duplication Check
    boolean existsByToUserAndFromUser(Members toUser, long fromUser);


    @Query("SELECT new yoon.capstone.application.common.dto.response.FriendsReqResponse(f.friendIdx, m.email, m.username, m.profile, f.createdAt) " +
            "FROM Members m INNER JOIN Friends f ON m.memberIdx = f.fromUser WHERE f.toUser.memberIdx = :toUser")
    List<FriendsReqResponse> findAllRequestsByToUser(@Param("toUser") long toUser);



}
