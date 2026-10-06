package com.ddeleon2022166.BiblioApp.controller;

import com.ddeleon2022166.BiblioApp.dto.request.LibroRequest;
import com.ddeleon2022166.BiblioApp.dto.response.LibroResponse;
import com.ddeleon2022166.BiblioApp.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public ResponseEntity<Page<LibroResponse>> listarLibros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String categoria,
            Pageable pageable) {
        return ResponseEntity.ok(libroService.listarLibros(titulo, categoria, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> obtenerLibro(@PathVariable Long id) {
        return ResponseEntity.ok(libroService.obtenerLibroPorId(id));
    }

    @PostMapping
    public ResponseEntity<LibroResponse> crearLibro(@Valid @RequestBody LibroRequest request) {
        return new ResponseEntity<>(libroService.crearLibro(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponse> actualizarLibro(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return ResponseEntity.ok(libroService.actualizarLibro(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) {
        libroService.eliminarLibro(id);
        return ResponseEntity.noContent().build();
    }
}