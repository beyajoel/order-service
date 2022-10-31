package com.bent.orderservice.service;

import com.bent.orderservice.dto.InventoryResponse;
import com.bent.orderservice.dto.OrderRequest;
import com.bent.orderservice.mapper.OrderMapper;
import com.bent.orderservice.model.Order;
import com.bent.orderservice.model.OrderLineItems;
import com.bent.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = getOrderFromRequest(orderRequest);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        if (Boolean.TRUE.equals(areProductsInStock(skuCodes))) {
            orderRepository.save(order);
            log.info("Order {} is saved successfully!", order.getId());
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    public Order getOrderFromRequest(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(
                orderRequest.getOrderLineItemsDtoList()
                        .stream()
                        .map(orderMapper::fromRequest)
                        .toList());
        return order;
    }

    /**
     * @return true if product is in stock
     * @implNote This method Call inventory service, and place order if product is in stock
     */
    public Boolean areProductsInStock(List<String> skuCodes) {
        return Arrays
                .stream(Objects.requireNonNull(
                        webClient.get()
                                .uri("http://localhost:8081/api/inventory",
                                        uriBuilder -> uriBuilder
                                                .queryParam("skuCodes", skuCodes)
                                                .build())
                                .retrieve()
                                .bodyToMono(InventoryResponse[].class)
                                .block()))
                .allMatch(InventoryResponse::getIsInStock);
    }
}
