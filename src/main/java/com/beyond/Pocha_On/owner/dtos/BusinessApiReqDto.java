package com.beyond.Pocha_On.owner.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BusinessApiReqDto {

    private String b_no;        // 사업자등록번호 : -없이 10자리
    private String p_nm;        // 대표자명
    private String start_dt;    // 개업일자 : yyyyMMdd -> 형식 지켜야함
//   private String b_nm;     // 상호명        //앞뒤 공백 무시하고. (주)가 앞뒤로 붙어도 됨

//    private String p_nm2;       //대표자성명2
//    private String b_nm;        //상호
//    private String corp_no;     //법인등록번호
//    private String b_sector;    //주업태명
//    private String b_type;      //주종목명
//    private String b_adr;       //사업장주소

}
