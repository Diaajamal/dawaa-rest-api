package com.pharmacy.restapi.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserRequest {

    private String firstName;

    private String lastName;
    private String userName;

    private String password;

    private String phone;

    private String address;
}
