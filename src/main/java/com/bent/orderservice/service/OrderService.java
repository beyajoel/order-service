package com.bent.orderservice.service;

import com.bent.orderservice.dto.OrderRequest;
import com.bent.orderservice.mapper.OrderMapper;
import com.bent.orderservice.model.Order;
import com.bent.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(
                orderRequest.getOrderLineItemsDtoList()
                        .stream()
                        .map(orderMapper::fromRequest)
                        .toList());
        orderRepository.save(order);
        log.info("Order {} is saved successfully!", order.getId());
    }
}
