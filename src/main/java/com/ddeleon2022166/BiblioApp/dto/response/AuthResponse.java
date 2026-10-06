package com.ddeleon2022166.BiblioApp.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String nombre;
    private String rol;
}