package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Orders;
import yoon.capstone.application.entity.Payment;
import yoon.capstone.application.entity.Projects;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByProjects(Projects projects);

    Orders findOrdersByPayment(Payment payment);

}
