package yoon.capstone.application.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import yoon.capstone.application.presentation.MemberController;
import yoon.capstone.application.service.*;
import yoon.capstone.application.service.repository.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser("tester")
public class MemberValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberController memberController;


    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private FriendsService friendsService;
    @MockBean
    private FriendRepository friendRepository;
    @MockBean
    private ProjectService projectService;
    @MockBean
    private ProjectRepository projectRepository;
    @MockBean
    private OrderService orderService;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderFacade orderFacade;
    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    void 멤버_회원가입유효성테스트_이메일빈칸() throws Exception {
        String url = "/api/v1/members/check/test@test.com";

        String requestBody = "{\"toUser\":\"1\"}";

        mockMvc.perform(get(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
                );
    }

    @Test
    void 멤버_회원가입유효성테스트_이메일형식(){}

    @Test
    void 멤버_회원가입유효성테스트_이메일성공(){}

    @Test
    void 멤버_회원가입유효성테스트_비밀번호빈칸(){}

    @Test
    void 멤버_회원가입유효성테스트_비밀번호성공(){}

    @Test
    void 멤버_회원가입유효성테스트_비밀번호길이(){}

    @Test
    void 멤버_회원가입유효성테스트_이름빈칸(){}

    @Test
    void 멤버_회원가입유효성테스트_이름성공(){}

    @Test
    void 멤버_회원가입유효성테스트_이름길이(){}



}
