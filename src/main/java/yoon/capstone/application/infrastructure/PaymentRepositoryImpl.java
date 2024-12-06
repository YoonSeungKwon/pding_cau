package yoon.capstone.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import yoon.capstone.application.infrastructure.jpa.PaymentJpaRepository;
import yoon.capstone.application.service.repository.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;


}
