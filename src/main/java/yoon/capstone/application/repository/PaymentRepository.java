package yoon.capstone.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.domain.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findPaymentByOrderId(String orderId);

}
