package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Carts;

@Repository
public interface CartRepository extends JpaRepository<Carts, Long> {


}
