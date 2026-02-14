package com.beyond.Pocha_On.owner.repository;

import com.beyond.Pocha_On.owner.dtos.BusinessApiReqDto;
import com.beyond.Pocha_On.owner.dtos.BusinessApiResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//외부 API호출 //RestTemplate //API URL //JSON/XML 매핑
@Component
public class OwnerVerifyClient {

    private final RestTemplate restTemplate;
    @Autowired
    public OwnerVerifyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${external.business.serviceKey}")
    private String serviceKey;

    private static final String baseUrl = "https://api.odcloud.kr/api/nts-businessman/v1";


    public BusinessApiResDto verify(BusinessApiReqDto reqDto){
        String url = baseUrl + "?serviceKey=" + serviceKey;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        req요청 body + head를 entity에 담음
        HttpEntity<BusinessApiReqDto> entity = new HttpEntity<>(reqDto, headers);

        ResponseEntity<BusinessApiResDto> response = restTemplate.postForEntity(url, entity, BusinessApiResDto.class);

        return response.getBody();
    }
}
