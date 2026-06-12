package com.example.shoppingcartapi.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserSummaryDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
