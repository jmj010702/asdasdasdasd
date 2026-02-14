package com.beyond.Pocha_On.customerorder.controller;


import com.beyond.Pocha_On.customerorder.dto.OrderCreateDto;
import com.beyond.Pocha_On.customerorder.dto.OrderListDto;
import com.beyond.Pocha_On.customerorder.sercvice.OrderService;
import com.beyond.Pocha_On.customerorder.dto.OrderAddDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

@Autowired
    public OrderController(OrderService orderService) {
    this.orderService = orderService;
    }

//    1.주문 생성
    @PostMapping("/create")
    public UUID create(@RequestBody OrderCreateDto createDto){
    UUID groupId = orderService.create(createDto);
    return groupId;

    }

//    2.추가 주문
    @PostMapping("/add")
    public UUID add(@RequestBody OrderAddDto addDto){
    UUID existGroupId = orderService.add(addDto);
    return existGroupId;
    }

//    3. 주문내역조회
    @GetMapping("/list")
    public List<OrderListDto> list(@RequestParam UUID groupId){
    List<OrderListDto> listDto = orderService.list(groupId);
    return listDto;
    }








}
