package yoon.capstone.application.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.entity.Orders;
import yoon.capstone.application.entity.Projects;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o JOIN FETCH o.members JOIN FETCH o.payment " +
            "JOIN FETCH o.comments WHERE o.payment.paymentCode = :paymentCode")
    Optional<Orders> findOrdersByPaymentCodeWithFetch(@Param("paymentCode") String paymentCode);

    //Eager Loading Except Orders.Projects
    @Query("SELECT o FROM Orders o JOIN FETCH o.members JOIN FETCH o.payment JOIN FETCH o.comments " +
            "WHERE o.projects.projectIdx = :projectsIndex")
    List<Orders> findAllByProjectsIndex(@Param("projectsIndex") long projectIndex);

    @Query("DELETE FROM Orders o WHERE o.payment.paymentCode = :paymentCode")
    void deleteOrdersWithPaymentCode(@Param("paymentCode") String paymentCode);



}
