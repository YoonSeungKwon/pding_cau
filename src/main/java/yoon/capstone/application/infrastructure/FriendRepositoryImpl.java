package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.FriendsJpaRepository;
import yoon.capstone.application.service.repository.FriendRepository;

@Repository
@RequiredArgsConstructor
public class FriendRepositoryImpl implements FriendRepository {

    private final FriendsJpaRepository friendsJpaRepository;


}
