package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Members;

@Repository
public interface MemberRepository extends JpaRepository<Members, Long> {

    Members getMembersByEmail(String email);

}
