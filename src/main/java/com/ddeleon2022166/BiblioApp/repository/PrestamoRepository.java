package com.ddeleon2022166.BiblioApp.repository;

import com.ddeleon2022166.BiblioApp.entity.Prestamo;
import com.ddeleon2022166.BiblioApp.entity.Usuario;
import com.ddeleon2022166.BiblioApp.enums.EstadoPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    int countByUsuarioAndEstado(Usuario usuario, EstadoPrestamo estado);
    List<Prestamo> findByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);
    List<Prestamo> findByEstadoAndFechaDevolucionEsperadaBefore(EstadoPrestamo estado, LocalDate fecha);
    List<Prestamo> findByUsuarioIdAndEstadoAndFechaDevolucionEsperadaBefore(Long usuarioId, EstadoPrestamo estado, LocalDate fecha);
}