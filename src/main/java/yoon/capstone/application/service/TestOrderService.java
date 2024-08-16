package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import yoon.capstone.application.dto.request.OrderDto;
import yoon.capstone.application.dto.response.OrderMessageDto;
import yoon.capstone.application.dto.response.ProjectCache;
import yoon.capstone.application.entity.*;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.OrderException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.OrderRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.security.JwtAuthentication;

import java.lang.reflect.Member;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TestOrderService {

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;

    @Value("${SERVICE_URL}")
    private String serviceUrl;

    @Value("${RABBITMQ_EXCHANGE_NAME}")
    private String exchange;

    @Value("${RABBITMQ_ROUTING_KEY}")
    private String routingKey;

    private final AesBytesEncryptor aesBytesEncryptor;

    private final RabbitTemplate rabbitTemplate;

    private final ProjectsRepository projectsRepository;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final RedissonClient redissonClient;

    private ProjectCache toCache(Projects projects){
        return new ProjectCache(projects.getProjectIdx(), projects.getTitle(), projects.getCurrentAmount(),
                projects.getGoalAmount(), projects.getParticipantsCount());
    }

    @Transactional
    public String kakaoPayment(OrderDto dto, String tempTid, Members memberDto){

        String paymentCode = UUID.randomUUID().toString();

        //Read Cache Or Cache Warm
        RBucket<ProjectCache> rBucket = redissonClient.getBucket("projects::" + dto.getProjectIdx());
        if (rBucket.get() == null) {
            Projects projects = projectsRepository.findProjectsByProjectIdx(dto.getProjectIdx());
            rBucket.set(toCache(projects), Duration.ofMinutes(10L));
        }
        //

        ProjectCache projects = rBucket.get();

        if(projects.getCurrentAmount() + dto.getTotal() > projects.getGoalAmount())
            throw new OrderException("목표금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("cid", "TC0ONETIME");
        map.add("partner_order_id", paymentCode);
        map.add("partner_user_id", memberDto.getEmail());
        map.add("quantity", 1);
        map.add("total_amount", dto.getTotal());
        map.add("tax_free_amount", dto.getTotal());
        map.add("approval_url", serviceUrl + "/api/v1/payment/success/" + paymentCode);
        map.add("cancel_url", serviceUrl + "/api/v1/payment/cancel/" + paymentCode);
        map.add("fail_url", serviceUrl + "/api/v1/payment/failure/" + paymentCode);
        map.add("item_name", projects.getTitle());

        //RestTemplate Event

        //

        byte[] encryptByte = aesBytesEncryptor.encrypt(tempTid.getBytes(StandardCharsets.UTF_8));
        String tid = Base64.getEncoder().encodeToString(encryptByte);

        OrderMessageDto message = new OrderMessageDto(dto.getProjectIdx(), memberDto.getMemberIdx()
        , dto.getTotal(), dto.getMessage(), tid, paymentCode);

        RBucket<OrderMessageDto> ordersBucket = redissonClient.getBucket("order::" + paymentCode);

        ordersBucket.set(message, Duration.ofMinutes(10L));

        return paymentCode;
    }

    @Transactional
    public void kakaoPaymentAccess(String id, String token){

        RBucket<OrderMessageDto> orderBucket = redissonClient.getBucket("order::"+id);
        OrderMessageDto dto = orderBucket.get();

        //RLock
        RLock rLock = redissonClient.getLock("projects"+dto.getProjectIdx());
        try {
            boolean available = rLock.tryLock(60L, 1L, TimeUnit.SECONDS);
            if(!available) throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            RBucket<ProjectCache> projectsRBucket = redissonClient.getBucket("projects::"+dto.getProjectIdx());
            ProjectCache projects = projectsRBucket.get();
            if(projects.getGoalAmount() < projects.getCurrentAmount() + dto.getTotal()){
                throw new OrderException("목표 금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);
            }else{
                projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
                projects.setParticipantsCount(projects.getParticipantsCount()+1);
                projectsRBucket.set(projects);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(rLock.isHeldByCurrentThread())
                rLock.unlock();
        }

        //결제 RestTemplate 요청

        rabbitTemplate.convertAndSend(exchange, routingKey, dto);

    }

    @Transactional
    @RabbitListener(queues = "${RABBITMQ_QUEUE_NAME}")
    public void messageListener(OrderMessageDto dto){

        Members members = memberRepository.findMembersByMemberIdx(dto.getMemberIdx()).orElseThrow(() -> new UsernameNotFoundException(null));
        Projects projects = projectsRepository.findProjectsByProjectIdxWithLock(dto.getProjectIdx());

        Orders orders = Orders.builder()
                .projects(projects)
                .members(members)
                .build();


        Payment payment = Payment.builder()
                .cost(dto.getTotal())
                .paymentCode(dto.getPaymentCode())
                .tid(dto.getTid())
                .orders(orders)
                .build();

        Comments comments = Comments.builder()
                .content(dto.getMessage())
                .orders(orders)
                .build();

        orders.setComments(comments);
        orders.setPayment(payment);

        projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
        projects.setParticipantsCount(projects.getParticipantsCount()+1);

        orderRepository.save(orders);
        projectsRepository.save(projects);

    }

}
