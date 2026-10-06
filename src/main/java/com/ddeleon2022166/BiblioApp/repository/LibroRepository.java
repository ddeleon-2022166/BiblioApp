package com.ddeleon2022166.BiblioApp.repository;

import com.ddeleon2022166.BiblioApp.entity.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByIdAndActivoTrue(Long id);

    @Query("SELECT l FROM Libro l WHERE l.activo = true AND " +
            "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) AND " +
            "LOWER(l.categoria) LIKE LOWER(CONCAT('%', :categoria, '%'))")
    Page<Libro> buscarConFiltros(@Param("titulo") String titulo, @Param("categoria") String categoria, Pageable pageable);

    boolean existsByIsbnAndActivoTrue(String isbn);
}