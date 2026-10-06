package com.ddeleon2022166.BiblioApp.service;

import com.ddeleon2022166.BiblioApp.dto.request.PrestamoRequest;
import com.ddeleon2022166.BiblioApp.entity.Libro;
import com.ddeleon2022166.BiblioApp.entity.Usuario;
import com.ddeleon2022166.BiblioApp.enums.EstadoPrestamo;
import com.ddeleon2022166.BiblioApp.enums.EstadoUsuario;
import com.ddeleon2022166.BiblioApp.enums.Rol;
import com.ddeleon2022166.BiblioApp.exception.BusinessRuleException;
import com.ddeleon2022166.BiblioApp.repository.LibroRepository;
import com.ddeleon2022166.BiblioApp.repository.PrestamoRepository;
import com.ddeleon2022166.BiblioApp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrestamoServiceTest {

    @Mock
    private PrestamoRepository prestamoRepository;
    @Mock
    private LibroRepository libroRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PrestamoService prestamoService;

    private Usuario lector;
    private Libro libro;
    private PrestamoRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(prestamoService, "diasLimite", 14);
        ReflectionTestUtils.setField(prestamoService, "limiteActivosLector", 3);

        lector = new Usuario(1L, "Lector", "lector@test.com", "pass", EstadoUsuario.ACTIVO, Rol.LECTOR);
        libro = new Libro(1L, "123", "Titulo", "Autor", "Cat", 5, 5, true);

        request = new PrestamoRequest();
        request.setUsuarioId(1L);
        request.setLibroId(1L);
    }

    @Test
    void prestamoDenegadoPorLimiteDeLector() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(libroRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(libro));
        when(prestamoRepository.findByUsuarioIdAndEstadoAndFechaDevolucionEsperadaBefore(1L, EstadoPrestamo.ACTIVO, java.time.LocalDate.now()))
                .thenReturn(Collections.emptyList());
        when(prestamoRepository.countByUsuarioAndEstado(lector, EstadoPrestamo.ACTIVO)).thenReturn(3);

        assertThrows(BusinessRuleException.class, () -> prestamoService.registrarPrestamo(request));
    }

    @Test
    void prestamoDenegadoPorFaltaDeStock() {
        libro.setStockDisponible(0);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(lector));
        when(libroRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(libro));
        when(prestamoRepository.findByUsuarioIdAndEstadoAndFechaDevolucionEsperadaBefore(1L, EstadoPrestamo.ACTIVO, java.time.LocalDate.now()))
                .thenReturn(Collections.emptyList());
        when(prestamoRepository.countByUsuarioAndEstado(lector, EstadoPrestamo.ACTIVO)).thenReturn(1);

        assertThrows(BusinessRuleException.class, () -> prestamoService.registrarPrestamo(request));
    }
}