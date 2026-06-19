package com.example.ecommercebackend.mapper;

import com.example.ecommercebackend.dto.PaymentDto;
import com.example.ecommercebackend.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentMapper {

    private final ModelMapper modelMapper;

    public PaymentDto toPaymentDto(Payment payment) {
        return modelMapper.map(payment, PaymentDto.class);
    }

    public Payment toPayment(PaymentDto paymentDto) {
        return modelMapper.map(paymentDto, Payment.class);
    }
}
