package com.ddeleon2022166.BiblioApp.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LibroResponse {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private String categoria;
    private Integer stockTotal;
    private Integer stockDisponible;
}