package com.ddeleon2022166.BiblioApp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LibroRequest {
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    @NotBlank(message = "El autor es obligatorio")
    private String autor;
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
    @NotNull(message = "El stock total es obligatorio")
    @PositiveOrZero(message = "El stock debe ser cero o positivo")
    private Integer stockTotal;
}