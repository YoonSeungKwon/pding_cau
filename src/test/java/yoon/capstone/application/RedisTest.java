package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yoon.capstone.application.dto.request.OrderDto;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.OrderRepository;
import yoon.capstone.application.repository.PaymentRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.service.OrderService;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class RedisTest {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void test() throws InterruptedException {

        int testSize = 200;
        Random random = new Random();

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failure = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(15);
        CountDownLatch countDownLatch = new CountDownLatch(testSize);

        for(int i=0; i<testSize; i++){
            executorService.execute(()->{
                try{
                    Members members = memberRepository.findMembersByMemberIdx(2000+random.nextInt(2000)).orElseThrow();

                    OrderDto dto = new OrderDto(506, random.nextInt(100,1000), "test");

                    String code = String.valueOf(orderService.kakaoPayment(dto));

                    System.out.println(code);
//                    orderService.kakaoPaymentAccess(code, "tokenTest");

                    success.incrementAndGet();
                }catch (Exception e){

                    failure.incrementAndGet();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }



        countDownLatch.await();

        System.out.println("Success :" + success);
        System.out.println("Failure :" + failure);

    }



}
