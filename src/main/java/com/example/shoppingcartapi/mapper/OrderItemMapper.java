package com.example.shoppingcartapi.mapper;

import com.example.shoppingcartapi.dto.OrderItemDto;
import com.example.shoppingcartapi.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemMapper {

    private final ModelMapper modelMapper;

    public OrderItem toOrderItem(OrderItemDto orderItemDto) {
        return modelMapper.map(orderItemDto, OrderItem.class);
    }

    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return modelMapper.map(orderItem, OrderItemDto.class);
    }
}
