package com.beyond.Pocha_On.owner.controller;

import com.beyond.Pocha_On.common.auth.JwtTokenProvider;
import com.beyond.Pocha_On.owner.dtos.*;
import com.beyond.Pocha_On.owner.service.OwnerLoginService;
import com.beyond.Pocha_On.owner.service.OwnerMyPageService;
import com.beyond.Pocha_On.owner.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owner")
public class OwnerController {
    private final OwnerService ownerService;
    private final OwnerLoginService ownerLoginService;
    private final OwnerMyPageService ownerMyPageService;

    @Autowired
    public OwnerController(OwnerService ownerService, OwnerLoginService ownerLoginService, JwtTokenProvider jwtTokenProvider, OwnerMyPageService ownerMyPageService) {
        this.ownerService = ownerService;
        this.ownerLoginService = ownerLoginService;
        this.ownerMyPageService = ownerMyPageService;
    }

    //    사업자등록번호 진위확인 OwnerController -> OwnerService -> OwnerVerifyClient
    @PostMapping("/business/verify")
    public BusinessApiResDto validateBusinessNumber(@RequestBody BusinessApiReqDto reqDto) {
        return ownerService.verify(reqDto);
    }

    @PostMapping("/baseLogin")
    public TokenDto baseLogin(@RequestBody BaseLoginDto dto) {
        return ownerLoginService.baseLogin(dto);
    }

    //    at만료시 redirect, rt기반으로 at재발급
    @PostMapping("/refresh")
    public TokenDto refresh(@RequestHeader("Authorization") String bearer) {
        String refreshToken = bearer.substring(7);
        return ownerLoginService.refresh(refreshToken);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void ownerCreate(@RequestBody OwnerCreateDto dto) {
        ownerLoginService.ownerSave(dto);
    }

//    mypage 조회
    @GetMapping("/myPage")
    public ResponseEntity<MyPageResDto> getMyPage(){
        MyPageResDto response = ownerMyPageService.getMyPage();
        return ResponseEntity.ok(response);
    }

//    mypage에서 전화번호 변경
    @PutMapping("/myPage/updatePhoneNum")
    public ResponseEntity<MyPageResDto> updatePhoneNum(@RequestBody MyPageUpdatePhoneNumDto reqDto){
        MyPageResDto response = ownerMyPageService.myPageUpdatePhoneNum(reqDto);
        return ResponseEntity.ok(response);
    }

//    mypage에서 비밀번호 변경
    @PutMapping("/myPage/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordReqDto reqDto){
        ownerMyPageService.myPageUpdatePassword(reqDto);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }


}
