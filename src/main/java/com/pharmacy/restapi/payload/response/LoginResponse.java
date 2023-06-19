package com.pharmacy.restapi.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private UUID id;
    private String userName;
    private String role;
    private String token;

}
