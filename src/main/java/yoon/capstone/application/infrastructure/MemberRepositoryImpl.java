package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.MemberJpaRepository;
import yoon.capstone.application.service.repository.MemberRepository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;




}
