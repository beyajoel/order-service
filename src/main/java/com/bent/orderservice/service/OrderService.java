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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = getOrderFromRequest(orderRequest);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        Boolean allProductsInStock = Arrays.stream(areProductsInStock(skuCodes))
                .allMatch(InventoryResponse::getIsInStock);

        if (Boolean.TRUE.equals(allProductsInStock)) {
            log.info("Order {} is saved successfully!", order.getId());
            orderRepository.save(order);
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
     * @return InventoryResponse array
     * @implNote This method Call inventory service, and place order if product is in stock
     */
    public InventoryResponse[] areProductsInStock(List<String> skuCodes) {
        return webClientBuilder.build().get()
                .uri("http://localhost:8083/api/inventory",
                        uriBuilder -> uriBuilder
                                .queryParam("skuCodes", skuCodes)
                                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }
}
