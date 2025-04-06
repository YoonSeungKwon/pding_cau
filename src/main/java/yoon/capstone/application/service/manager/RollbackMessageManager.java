package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import yoon.capstone.application.common.dto.response.OrderMessageDto;
import yoon.capstone.application.config.RabbitMQConfig;

@Service
@Slf4j
@RequiredArgsConstructor
public class RollbackMessageManager implements MessageManager{

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(Object o) {
        OrderMessageDto dto = (OrderMessageDto) o;
        CorrelationData correlationData = new CorrelationData(dto.getPaymentCode());
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.ROLLBACK_EXCHANGE, RabbitMQConfig.ROLLBACK_QUEUE, dto, msg -> {
                msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);return msg;}, correlationData);
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
