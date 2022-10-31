package com.bent.orderservice.controller;

import com.bent.orderservice.dto.OrderRequest;
import com.bent.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order %s is Placed Successfully!".formatted(orderRequest.getOrderLineItemsDtoList().get(0).getSkuCode());
    }
}
