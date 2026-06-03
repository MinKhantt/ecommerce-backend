package com.example.shoppingcartapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
