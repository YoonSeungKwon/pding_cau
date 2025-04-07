package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.common.dto.request.KakaoApproveRequest;
import yoon.capstone.application.common.dto.request.KakaoReadyRequest;
import yoon.capstone.application.common.dto.response.KakaoPayResponse;
import yoon.capstone.application.common.dto.response.KakaoResultResponse;
import yoon.capstone.application.common.dto.response.PayResponse;

@Service
@RequiredArgsConstructor
public class KakaoOrderManager implements OrderManager{

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;
    @Value("${SERVICE_URL}")
    private String serviceUrl;

    private final String prepareUrl = "https://open-api.kakaopay.com/online/v1/payment/ready";

    private final String accessUrl = "https://open-api.kakaopay.com/online/v1/payment/approve";

    @Override
    public void orderCancel() {

    }

    @Override
    public PayResponse orderPrepare(long index, String name, String paymentCode, int total) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.set("Content-type", "application/json");
        headers.set("Authorization", "SECRET_KEY "+ admin_key);

        KakaoReadyRequest kakaoRequest = new KakaoReadyRequest("TC0ONETIME",
                paymentCode, String.valueOf(index), name, 1, total, total,
                serviceUrl + "/api/v1/payment/success/"+paymentCode, serviceUrl + "/api/v1/payment/cancel/"+paymentCode,
                serviceUrl + "/api/v1/payment/failure/"+paymentCode);


        HttpEntity<KakaoReadyRequest> request = new HttpEntity<>(kakaoRequest, headers);

        return restTemplate.postForObject(
                prepareUrl,
                request,
                KakaoPayResponse.class
        );
    }

    @Override
    public void orderAccess(long index, String code, String tid, String token) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        headers.set("Content-type", "application/json");
        headers.set("Authorization", "DEV_SECRET_KEY " + admin_key);

        KakaoApproveRequest kakaoApproveRequest = new KakaoApproveRequest("TC0ONETIME", tid,
                code, String.valueOf(index), token);

        HttpEntity<KakaoApproveRequest> request = new HttpEntity<>(kakaoApproveRequest, headers);

        restTemplate.postForObject(
                accessUrl,
                request,
                KakaoResultResponse.class
        );
    }
}
