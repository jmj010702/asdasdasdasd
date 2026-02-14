package com.beyond.Pocha_On.customerTable.controller;

import com.beyond.Pocha_On.customerTable.dtos.TableSelectDto;
import com.beyond.Pocha_On.customerTable.dtos.TableTokenDto;
import com.beyond.Pocha_On.customerTable.dtos.CustomerTableStatusListDto;
import com.beyond.Pocha_On.customerTable.service.CustomerTableService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customerTable")
public class CustomerTableController {

    private final CustomerTableService customerTableService;

    @Autowired
    public CustomerTableController(CustomerTableService customerTableService) {
        this.customerTableService = customerTableService;
    }


    //    점주 화면기준에서 테이블 현황 json으로 받아옴 //06.02.05
    @GetMapping("/tableStatusList")
    public ResponseEntity<?> cTableStatusList(Claims claims) {
        Long storeId = claims.get("storeId", Long.class);
        List<CustomerTableStatusListDto> customerTableStatusListDtoList = customerTableService.customerTableStatusList(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(customerTableStatusListDtoList);
    }
    //  매장 전체 테이블 현황 조회
    @GetMapping("/list")
    public ResponseEntity<?> cTableStatusList(@RequestAttribute("storeId") Long storeId) {
        List<CustomerTableStatusListDto> dtoList = customerTableService.customerTableStatusList(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }

    //    단일 테이블 상세 조회
    @GetMapping("/{tableId}")
    public ResponseEntity<?> customerTableDetail(@PathVariable Long tableId, @RequestAttribute("storeId") Long storeId) {
        CustomerTableStatusListDto dto = customerTableService.getTableStatus(storeId, storeId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PostMapping("/select")
    public TableTokenDto selectTable(
            @RequestAttribute String stage,
            @RequestAttribute Long storeId,
            @RequestBody TableSelectDto dto
    ) {
        return customerTableService.selectTable(stage, storeId, dto);
    }
}



/*
[
  {
    "cTableId": 1,
    "tableStatus": "USING",
    "groupCreateAt": "2026-02-03T10:30:00",
    "orderingList": [
      {
        "orderingId": 1,
        "totalPrice": 25000,
        "orderStatus": "PENDING",
        "paymentState": "DONE",
        "orderingOptionList": [
          {
            "menuName": "짜장면",
            "quantity": 2,
            "options": ["곱빼기"]
          },
          {
            "menuName": "탕수육",
            "quantity": 1,
            "options": []
          }
        ]
      }
    ]
  },
  {
    "cTableId": 2,
    "tableStatus": "STANDBY",
    "groupCreateAt": null,
    "orderingList": []
  }
]
 */
