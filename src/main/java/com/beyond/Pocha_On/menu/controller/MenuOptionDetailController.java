package com.beyond.Pocha_On.menu.controller;

import com.beyond.Pocha_On.menu.dtos.MenuOptionDetailReqDto;
import com.beyond.Pocha_On.menu.service.MenuOptionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/store/menu/option")
public class MenuOptionDetailController {
    private final MenuOptionDetailService menuOptionDetailService;
    @Autowired
    public MenuOptionDetailController(MenuOptionDetailService menuOptionDetailService) {
        this.menuOptionDetailService = menuOptionDetailService;
    }

    //    owner 메뉴옵션상세 추가
    @PostMapping("/{menuOptionId}/detail")
    public ResponseEntity<?> createOptionDetail(@PathVariable Long optionId, @RequestBody MenuOptionDetailReqDto reqDto) throws AccessDeniedException {
    Long optionDetailId = menuOptionDetailService.createOptionDetail(optionId, reqDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(optionDetailId);
    }

    //    owner 메뉴옵션상세 수정
    @PutMapping("/detail/{detailId}")
    public ResponseEntity<?> updateOptionDetail(@PathVariable Long optionDetailId, @RequestBody MenuOptionDetailReqDto reqDto) throws AccessDeniedException {
    menuOptionDetailService.updateOptionDetail(optionDetailId, reqDto);
        return ResponseEntity.status(HttpStatus.OK).body("메뉴옵션상세 수정 완료되었습니다");
    }

    // owner 메뉴옵션상세 삭제
    @DeleteMapping("/detail/{detailId}")
    public ResponseEntity<?> deleteOptionDetail(@PathVariable Long optionDetailId) throws AccessDeniedException {
    menuOptionDetailService.deleteOptionDetail(optionDetailId);
        return ResponseEntity.status(HttpStatus.OK).body("메뉴옵션상세가 삭제되었습니다");
    }
}
