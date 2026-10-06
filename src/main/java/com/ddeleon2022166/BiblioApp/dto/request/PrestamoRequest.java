package com.ddeleon2022166.BiblioApp.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrestamoRequest {
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;
}