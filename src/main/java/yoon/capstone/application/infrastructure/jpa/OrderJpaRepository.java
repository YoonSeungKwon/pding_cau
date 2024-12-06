package yoon.capstone.application.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.service.domain.Orders;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<Orders, Long> {

    //Eagle Loading - Projects
    @Query("SELECT o FROM Orders o JOIN FETCH o.members " +
            "JOIN FETCH o.comments JOIN FETCH o.payment p WHERE p.paymentCode = :paymentCode")
    Optional<Orders> findOrdersByPaymentCodeWithFetchJoin(@Param("paymentCode") String paymentCode);

    //Eager Loading + Projects
    @Query("SELECT o FROM Orders o JOIN FETCH o.members JOIN FETCH o.payment JOIN FETCH o.comments " +
            "WHERE o.projects.projectIdx = :projectsIndex")
    List<Orders> findAllByProjectsIndexWithFetchJoin(@Param("projectsIndex") long projectIndex);

    @Query("DELETE FROM Orders o WHERE o.payment.paymentCode = :paymentCode")
    void deleteOrdersWithPaymentCode(@Param("paymentCode") String paymentCode);



}
