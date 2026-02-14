package com.beyond.Pocha_On.owner.service;

import com.beyond.Pocha_On.common.service.EmailAuthService;
import com.beyond.Pocha_On.owner.domain.Owner;
import com.beyond.Pocha_On.owner.dtos.MyPageResDto;
import com.beyond.Pocha_On.owner.dtos.MyPageUpdatePhoneNumDto;
import com.beyond.Pocha_On.owner.dtos.UpdatePasswordReqDto;
import com.beyond.Pocha_On.owner.repository.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerMyPageService {
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailAuthService emailAuthService;
    @Autowired
    public OwnerMyPageService(OwnerRepository ownerRepository, PasswordEncoder passwordEncoder, EmailAuthService emailAuthService) {
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailAuthService = emailAuthService;
    }

//    mypage 조회
    public MyPageResDto getMyPage() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        return MyPageResDto.fromEntity(owner);
    }

//    mypage에서 전화번호 변경
    public MyPageResDto myPageUpdatePhoneNum(MyPageUpdatePhoneNumDto reqDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new EntityNotFoundException("Owner not found"));
        owner.updatePhoneNum(reqDto.getPhoneNumber());
        return MyPageResDto.fromEntity(owner);
    }

//    mypage에서 비밀번호 변경
    public ResponseEntity<?> myPageUpdatePassword(UpdatePasswordReqDto reqDto){
        if(reqDto.getNewPassword().length() < 8)
            throw new RuntimeException("비밀번호 8자 이상 필요");
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        // 1. 이메일 인증 여부 확인
        emailAuthService.checkVerified(email);

        // 2. 회원 조회
        Owner owner = ownerRepository.findByOwnerEmail(email).orElseThrow(() -> new RuntimeException("Owner not found"));

        if(passwordEncoder.matches(reqDto.getOldPassword(), reqDto.getNewPassword())) {
            throw new RuntimeException("기존 비밀번호와 같은 비밀번호 입력");
        }
        // 3. 비밀번호 암호화 후 변경
        String encoded = passwordEncoder.encode(reqDto.getNewPassword());
        owner.changePassword(encoded);

        ownerRepository.save(owner);

        // 4. 인증 상태 제거 (재사용 방지)
        emailAuthService.clear(email);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 변경 완료");
    }
}
