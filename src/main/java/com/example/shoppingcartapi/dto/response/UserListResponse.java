package com.example.shoppingcartapi.dto.response;

import com.example.shoppingcartapi.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse implements Serializable {
    private List<UserDto> users;
}