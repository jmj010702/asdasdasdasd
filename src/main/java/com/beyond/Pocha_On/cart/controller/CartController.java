package com.beyond.Pocha_On.cart.controller;


import com.beyond.Pocha_On.cart.dto.cart_dto.CartCreateDto;
import com.beyond.Pocha_On.cart.dto.cart_dto.CartDto;
import com.beyond.Pocha_On.cart.dto.cart_dto.CartLineDeleteDto;
import com.beyond.Pocha_On.cart.dto.cart_dto.CartUpdateDto;
import com.beyond.Pocha_On.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    //    1.카트 생성
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CartCreateDto createDto) {
        cartService.cartCreate(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }


    //    2. 카트 조회
    @GetMapping("/{tableId}")
    public CartDto cartAll(@PathVariable Long tableId) {
        CartDto cartDto = cartService.cartAll(tableId);
        return cartDto;
    }

    //    3. 카트 수정(수량변경)
    @PatchMapping("/quantity")
    public ResponseEntity<?> UpdateQuantity(@RequestBody CartUpdateDto updateDto) {
        cartService.UpdateQuantity(updateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    //    4. 특정 줄 삭제
    @DeleteMapping("/line")
    public ResponseEntity<?> LineDelete(@RequestBody CartLineDeleteDto deleteDto) {
        cartService.LineDelete(deleteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");
    }

    //    5. 카트 비우기
    @DeleteMapping("/{tableId}")
    public ResponseEntity<?> CartClear(@PathVariable Long tableId) {
        cartService.CartClear(tableId);
        return ResponseEntity.status(HttpStatus.CREATED).body("ok");

    }
}
