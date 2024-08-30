package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.entity.*;
import yoon.capstone.application.repository.FriendsRepository;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.OrderRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.security.JwtProvider;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ConcurrencyTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    ProjectsRepository projectsRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Transactional
    List<Members> setting(){

        Projects projects = projectsRepository.findProjectsByProjectIdx(501);
        List<Members> list = friendsRepository.findAllByFromUserWithFetchJoin(29).stream().map(Friends::getToUser).toList();

        for(int i=0; i<list.size(); i++){
            Members currentMember = list.get(i);

            Payment payment = Payment.builder()
                    .paymentCode(currentMember.getEmail())
                    .tid(currentMember.getUsername())
                    .cost(100)
                    .build();

            Comments comments = Comments.builder()
                    .content("Test")
                    .build();

            Orders orders = Orders.builder()
                    .projects(projects)
                    .members(currentMember)
                    .build();

            orders.setPayment(payment);
            orders.setComments(comments);

            orderRepository.save(orders);
        }

        System.out.println("테스트 멤버 수 : " +list.size());
        return list;
    }

    @Test
    @Transactional
    void orderConcurrencyTest() throws InterruptedException {
        Projects projects = projectsRepository.findProjectsByProjectIdx(501);


        List<Members> members = friendsRepository.findAllByFromUserWithFetchJoin(29).stream().map(Friends::getToUser).toList();



        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(members.size());

        for(Members m: members){
            executorService.execute(()->{
                try{
//                    orderService.kakaoPaymentAccess(m.getEmail(), null);
                    successCount.incrementAndGet();
                }catch(Exception e) {
                    System.out.println(e.getMessage());
                    failureCount.incrementAndGet();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        System.out.println("Success : " +successCount + "/" + members.size());
        System.out.println("Failure : " +failureCount + "/" + members.size());
        System.out.println("Amount : " + projects.getCurrentAmount() + "원");
        System.out.println("Total : " + projects.getParticipantsCount() + "명");

    }

    @Test
    @Transactional
    void queryTest(){

        Members members = memberRepository.findMembersByMemberIdx(30).orElseThrow(()->new UsernameNotFoundException(null));
        Orders orders = orderRepository.findOrdersByPaymentCodeWithFetchJoin(members.getEmail()).orElseThrow(()->new UsernameNotFoundException("Order Not Found"));

        System.out.println(orders.getOrderIdx());

    }


}
