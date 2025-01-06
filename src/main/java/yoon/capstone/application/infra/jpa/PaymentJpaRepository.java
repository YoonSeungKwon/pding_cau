package yoon.capstone.application.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.service.domain.Payment;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

}
