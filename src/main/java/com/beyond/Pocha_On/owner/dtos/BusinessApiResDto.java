package com.beyond.Pocha_On.owner.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BusinessApiResDto {

    private String status_code;     //API 상태코드 => OK, error
    private String message;         //실패시 응답메세지
    private List<BusinessData> data;


    public static class  BusinessData {
        private String b_no;          // 사업자등록번호
        private String b_stt;         // 사업자 상태
        private String b_stt_cd;
        private String tax_type;      // 과세 유형
        private String tax_type_cd;
        private String end_dEt;        // 폐업일 (없으면 "")
    }
}
