package yoon.capstone.application.service.repository;

import yoon.capstone.application.service.domain.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {


    Optional<Orders> findOrder(String paymentCode);

    List<Orders> findAllOrders(long projectIndex);

    void cancelOrder(String paymentCode);

}
