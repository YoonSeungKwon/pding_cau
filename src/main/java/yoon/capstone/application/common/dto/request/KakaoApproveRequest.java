package yoon.capstone.application.common.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoApproveRequest {

    private String cid;
    private String tid;
    private String partner_order_id;
    private String partner_user_id;
    private String pg_token;
}
