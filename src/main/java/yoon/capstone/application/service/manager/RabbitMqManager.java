package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yoon.capstone.application.common.dto.response.OrderMessageDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMqManager implements MessageManager{

    @Value("${RABBITMQ_EXCHANGE_NAME}")
    private String exchange;

    @Value("${RABBITMQ_ROUTING_KEY}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;


    @Override
    public void publish(Object o) {
        OrderMessageDto dto = (OrderMessageDto) o;
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, dto);
        }catch (AmqpException e) {                  //rabbit
            System.out.println("Message sending failed: " + e.getMessage());
            log.error("메시지큐 전송 실패" +
                    "\n memberIndex  " + dto.getMemberIdx() +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n paymentCode  " + dto.getPaymentCode() +
                    "\n total        " + dto.getTotal() +
                    "\n tid          " + dto.getTid() +
                    "\n message      " + dto.getMessage());
        }
    }

    @Override
    public void subscribe() {

    }
}
