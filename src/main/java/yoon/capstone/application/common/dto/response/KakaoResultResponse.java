package yoon.capstone.application.common.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class KakaoResultResponse {

    private String aid;

    private String tid;		                //결제 고유 번호

    private String cid;		                //가맹점 코드

    private String sid;		                //정기 결제용 ID, 정기 결제 CID로 단건 결제 요청 시 발급

    private String partner_order_id;		//가맹점 주문번호, 최대 100자

    private String partner_user_id;		    //가맹점 회원 id, 최대 100자

    private String payment_method_type;		//결제 수단, CARD 또는 MONEY 중 하나

    private Amount amount;		            //결제 금액 정보

    private CardInfo card_info;	            //결제 상세 정보, 결제수단이 카드일 경우만 포함

    private String item_name;		        //상품 이름, 최대 100자

    private String item_code;		        //상품 코드, 최대 100자

    private Integer quantity;		        //상품 수량

    private LocalDateTime created_at;		//결제 준비 요청 시각

    private LocalDateTime approved_at;		//결제 승인 시각

    private String payload;		            //결제 승인 요청에 대해 저장한 값, 요청 시 전달된 내용


}
