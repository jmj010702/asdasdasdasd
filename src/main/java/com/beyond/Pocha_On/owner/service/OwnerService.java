package com.beyond.Pocha_On.owner.service;

import com.beyond.Pocha_On.owner.dtos.BusinessApiReqDto;
import com.beyond.Pocha_On.owner.dtos.BusinessApiResDto;
import com.beyond.Pocha_On.owner.repository.OwnerVerifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerService {
    private final OwnerVerifyClient ownerVerifyClient;
    @Autowired
    public OwnerService(OwnerVerifyClient ownerVerifyClient) {
        this.ownerVerifyClient = ownerVerifyClient;
    }

    public BusinessApiResDto verify(BusinessApiReqDto reqDto){
        BusinessApiResDto resDto = ownerVerifyClient.verify(reqDto);

        if(resDto == null){
            throw new IllegalStateException("사업자진위확인 API 응답이 없습니다.");
        }
        if(!resDto.getStatus_code().equals("OK")){
            throw new IllegalStateException("사업자 확인 실패" + resDto.getMessage());
        }
        if(resDto.getData() == null || resDto.getData().isEmpty()){
            throw new IllegalStateException("사업자 정보가 존재하지 않습니다.");
        }

//        // 실제 사업자 정보 추출
//        BusinessApiResDto.BusinessData businessData = resDto.getData().get(0);
//
//        // 폐업 여부 (end_dt가 비어있지 않으면 폐업)
//        if (businessData.get != null && !businessData.getEnd_dt().isEmpty()) {
//            throw new IllegalStateException("폐업된 사업자입니다.");
//        }
//
//        // 사업자 상태
//        if (!"계속사업자".equals(businessData.getB_stt())) {
//            throw new IllegalStateException(
//                    "유효하지 않은 사업자 상태: " + businessData.getB_stt()
//            );
//        }

        return resDto;
    }
}
