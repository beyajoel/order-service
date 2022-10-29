package com.bent.orderservice.mapper;

import com.bent.orderservice.dto.OrderLineItemsDto;
import com.bent.orderservice.model.OrderLineItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderLineItems fromRequest(OrderLineItemsDto orderLineItemsDto);

    OrderLineItemsDto toRequest(OrderLineItems orderLineItems);
}
