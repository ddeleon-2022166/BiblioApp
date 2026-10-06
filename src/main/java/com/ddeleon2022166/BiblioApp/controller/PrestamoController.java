package com.ddeleon2022166.BiblioApp.controller;

import com.ddeleon2022166.BiblioApp.dto.request.PrestamoRequest;
import com.ddeleon2022166.BiblioApp.dto.response.PrestamoResponse;
import com.ddeleon2022166.BiblioApp.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return new ResponseEntity<>(prestamoService.registrarPrestamo(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/devolucion")
    public ResponseEntity<PrestamoResponse> registrarDevolucion(@PathVariable Long id) {
        return ResponseEntity.ok(prestamoService.registrarDevolucion(id));
    }

    @GetMapping("/mis-prestamos")
    public ResponseEntity<List<PrestamoResponse>> obtenerMisPrestamos(Authentication authentication) {
        // La identidad se saca del token JWT y el SecurityContextHolder
        String email = authentication.getName();
        return ResponseEntity.ok(prestamoService.obtenerMisPrestamos(email));
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<PrestamoResponse>> obtenerPrestamosAtrasados() {
        return ResponseEntity.ok(prestamoService.obtenerPrestamosAtrasados());
    }
}