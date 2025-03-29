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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@WithMockUser("tester")
public class MemberApiTest {

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
    void 멤버API_이메일중복체크() throws Exception {

        String url = "/api/v1/members/check/test@test.com";

        String requestBody = "{\"toUser\":\"1\"}";

        mockMvc.perform(get(url)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
        );

    }

    @Test
    void 멤버API_회원가입() throws Exception {

        String url = "/api/v1/members/";

        String requestBody = "{\"email\":\"test@test.com\",\"password\":\"abcd1234\",\"name\":\"tester\",\"phone\":\"010-1234-5678\"}";

        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()
                );

    }

    @Test
    void 멤버API_유저검색() throws Exception {

        String url = "/api/v1/members/test@test.com";

        String requestBody = "{\"toUser\":\"1\"}";

        mockMvc.perform(get(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()
                );

    }

    @Test
    void 멤버API_로그인() throws Exception {

        String url = "/api/v1/members/login";

        String requestBody = "{\"email\":\"test@test.com\",\"password\":\"abcd1234\"}";


        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()
                );

    }

    @Test
    void 멤버API_로그아웃() throws Exception {

        String url = "/api/v1/members/logout";

        String requestBody = "{\"toUser\":\"1\"}";

        mockMvc.perform(get(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()
                );

    }
    @Test
    void 멤버API_프로필변경() throws Exception {

        String url = "/api/v1/members/profile";

        String requestBody = "{\"file\":\"file\"}";

        mockMvc.perform(post(url)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk()
                );

    }



}
