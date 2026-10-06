package com.ddeleon2022166.BiblioApp.service;

import com.ddeleon2022166.BiblioApp.dto.request.PrestamoRequest;
import com.ddeleon2022166.BiblioApp.dto.response.PrestamoResponse;
import com.ddeleon2022166.BiblioApp.entity.Libro;
import com.ddeleon2022166.BiblioApp.entity.Prestamo;
import com.ddeleon2022166.BiblioApp.entity.Usuario;
import com.ddeleon2022166.BiblioApp.enums.EstadoPrestamo;
import com.ddeleon2022166.BiblioApp.enums.EstadoUsuario;
import com.ddeleon2022166.BiblioApp.enums.Rol;
import com.ddeleon2022166.BiblioApp.exception.BusinessRuleException;
import com.ddeleon2022166.BiblioApp.exception.ResourceNotFoundException;
import com.ddeleon2022166.BiblioApp.repository.LibroRepository;
import com.ddeleon2022166.BiblioApp.repository.PrestamoRepository;
import com.ddeleon2022166.BiblioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${biblioteca.prestamo.dias-limite:14}")
    private int diasLimite;

    @Value("${biblioteca.prestamo.limite-activos-lector:3}")
    private int limiteActivosLector;

    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Libro libro = libroRepository.findByIdAndActivoTrue(request.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        verificarSancionPorAtraso(usuario);

        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            throw new BusinessRuleException("El usuario está SANCIONADO y no puede realizar préstamos");
        }

        if (usuario.getRol() == Rol.LECTOR) {
            int prestamosActivos = prestamoRepository.countByUsuarioAndEstado(usuario, EstadoPrestamo.ACTIVO);
            if (prestamosActivos >= limiteActivosLector) {
                throw new BusinessRuleException("El LECTOR ha alcanzado el límite de " + limiteActivosLector + " préstamos activos simultáneos");
            }
        }

        if (libro.getStockDisponible() <= 0) {
            throw new BusinessRuleException("No hay stock disponible para prestar este libro");
        }

        libro.setStockDisponible(libro.getStockDisponible() - 1);
        libroRepository.save(libro);

        Prestamo prestamo = Prestamo.builder()
                .usuario(usuario)
                .libro(libro)
                .fechaPrestamo(LocalDate.now())
                .fechaDevolucionEsperada(LocalDate.now().plusDays(diasLimite))
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        return mapToResponse(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponse registrarDevolucion(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessRuleException("El préstamo ya fue devuelto");
        }

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);

        Libro libro = prestamo.getLibro();
        libro.setStockDisponible(libro.getStockDisponible() + 1);
        if (libro.getStockDisponible() > libro.getStockTotal()) {
            throw new BusinessRuleException("Inconsistencia: El stock disponible superó el stock total");
        }

        libroRepository.save(libro);
        prestamoRepository.save(prestamo);

        verificarLevantamientoSancion(prestamo.getUsuario());

        return mapToResponse(prestamo);
    }

    public List<PrestamoResponse> obtenerMisPrestamos(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return prestamoRepository.findByUsuarioIdOrderByFechaPrestamoDesc(usuario.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<PrestamoResponse> obtenerPrestamosAtrasados() {
        return prestamoRepository.findByEstadoAndFechaDevolucionEsperadaBefore(EstadoPrestamo.ACTIVO, LocalDate.now())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void verificarSancionPorAtraso(Usuario usuario) {
        List<Prestamo> atrasados = prestamoRepository.findByUsuarioIdAndEstadoAndFechaDevolucionEsperadaBefore(
                usuario.getId(), EstadoPrestamo.ACTIVO, LocalDate.now());

        if (!atrasados.isEmpty() && usuario.getEstado() == EstadoUsuario.ACTIVO) {
            usuario.setEstado(EstadoUsuario.SANCIONADO);
            usuarioRepository.save(usuario);
            throw new BusinessRuleException("El usuario tiene préstamos atrasados. Su estado ha cambiado a SANCIONADO automáticamente.");
        }
    }

    private void verificarLevantamientoSancion(Usuario usuario) {
        if (usuario.getEstado() == EstadoUsuario.SANCIONADO) {
            List<Prestamo> atrasados = prestamoRepository.findByUsuarioIdAndEstadoAndFechaDevolucionEsperadaBefore(
                    usuario.getId(), EstadoPrestamo.ACTIVO, LocalDate.now());
            if (atrasados.isEmpty()) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
                usuarioRepository.save(usuario);
            }
        }
    }

    private PrestamoResponse mapToResponse(Prestamo prestamo) {
        return PrestamoResponse.builder()
                .id(prestamo.getId())
                .usuarioNombre(prestamo.getUsuario().getNombre())
                .libroTitulo(prestamo.getLibro().getTitulo())
                .fechaPrestamo(prestamo.getFechaPrestamo())
                .fechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada())
                .fechaDevolucionReal(prestamo.getFechaDevolucionReal())
                .estado(prestamo.getEstado().name())
                .build();
    }
}