package com.example.shoppingcartapi.mapper;

import com.example.shoppingcartapi.dto.OrderDto;
import com.example.shoppingcartapi.entity.Order;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMapper {

    private final ModelMapper modelMapper;

    public OrderDto toOrderDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    public Order toOrder(OrderDto orderDto) {
        return modelMapper.map(orderDto, Order.class);
    }
}
