package yoon.capstone.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.ProjectException;
import yoon.capstone.application.config.security.JwtProvider;
import yoon.capstone.application.infra.jpa.FriendsJpaRepository;
import yoon.capstone.application.infra.jpa.MemberJpaRepository;
import yoon.capstone.application.infra.jpa.OrderJpaRepository;
import yoon.capstone.application.infra.jpa.ProjectsJpaRepository;
import yoon.capstone.application.service.MemberService;
import yoon.capstone.application.service.OrderService;
import yoon.capstone.application.service.domain.*;

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
    MemberJpaRepository memberRepository;

    @Autowired
    FriendsJpaRepository friendsRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    ProjectsJpaRepository projectsRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderJpaRepository orderRepository;

    @Transactional
    List<Members> setting(){

        Projects projects = projectsRepository.findProjectsByProjectIdx(501).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
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
        Projects projects = projectsRepository.findProjectsByProjectIdx(501).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));


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
