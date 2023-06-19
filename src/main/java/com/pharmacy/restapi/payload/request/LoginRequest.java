package com.pharmacy.restapi.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class LoginRequest {
    @NonNull
    private String userName;
    @NonNull
    private String password;
}
