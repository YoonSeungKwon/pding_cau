package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.OrderJpaRepository;
import yoon.capstone.application.service.repository.OrderRepository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;


}
