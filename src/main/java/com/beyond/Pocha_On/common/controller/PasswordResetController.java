package com.beyond.Pocha_On.common.controller;


import com.beyond.Pocha_On.common.dtos.PasswordResetReqDto;
import com.beyond.Pocha_On.common.service.PasswordResetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/reset")
    public void reset(@RequestBody PasswordResetReqDto dto){
        passwordResetService.resetPassword(dto);
    }
}