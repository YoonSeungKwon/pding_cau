package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.OrderJpaRepository;
import yoon.capstone.application.service.domain.Orders;
import yoon.capstone.application.service.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;


    @Override
    public Optional<Orders> findOrder(String paymentCode) {
        return orderJpaRepository.findOrdersByPaymentCodeWithFetchJoin(paymentCode);
    }

    @Override
    public List<Orders> findAllOrders(long projectIndex) {
        return orderJpaRepository.findAllByProjectsIndexWithFetchJoin(projectIndex);
    }

    @Override
    public void cancelOrder(String paymentCode) {
        orderJpaRepository.deleteOrdersWithPaymentCode(paymentCode);
    }
}
