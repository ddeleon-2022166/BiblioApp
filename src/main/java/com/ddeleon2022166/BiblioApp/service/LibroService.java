package com.ddeleon2022166.BiblioApp.service;

import com.ddeleon2022166.BiblioApp.dto.request.LibroRequest;
import com.ddeleon2022166.BiblioApp.dto.response.LibroResponse;
import com.ddeleon2022166.BiblioApp.entity.Libro;
import com.ddeleon2022166.BiblioApp.exception.BusinessRuleException;
import com.ddeleon2022166.BiblioApp.exception.ResourceNotFoundException;
import com.ddeleon2022166.BiblioApp.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    public Page<LibroResponse> listarLibros(String titulo, String categoria, Pageable pageable) {
        // Si el controlador pasa null, lo convertimos a cadena vacía para evitar errores en PostgreSQL
        String filtroTitulo = (titulo == null) ? "" : titulo;
        String filtroCategoria = (categoria == null) ? "" : categoria;

        return libroRepository.buscarConFiltros(filtroTitulo, filtroCategoria, pageable)
                .map(this::mapToResponse);
    }

    public LibroResponse obtenerLibroPorId(Long id) {
        Libro libro = libroRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        return mapToResponse(libro);
    }

    public LibroResponse crearLibro(LibroRequest request) {
        if (libroRepository.existsByIsbnAndActivoTrue(request.getIsbn())) {
            throw new BusinessRuleException("El ISBN ya está registrado y activo");
        }

        Libro libro = Libro.builder()
                .isbn(request.getIsbn())
                .titulo(request.getTitulo())
                .autor(request.getAutor())
                .categoria(request.getCategoria())
                .stockTotal(request.getStockTotal())
                .stockDisponible(request.getStockTotal())
                .activo(true)
                .build();

        return mapToResponse(libroRepository.save(libro));
    }

    public LibroResponse actualizarLibro(Long id, LibroRequest request) {
        Libro libro = libroRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        int diferenciaStock = request.getStockTotal() - libro.getStockTotal();
        int nuevoStockDisponible = libro.getStockDisponible() + diferenciaStock;

        if (nuevoStockDisponible < 0) {
            throw new BusinessRuleException("La reducción del stock total afecta préstamos activos. Mínimo permitido: "
                    + (libro.getStockTotal() - libro.getStockDisponible()));
        }

        libro.setIsbn(request.getIsbn());
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setCategoria(request.getCategoria());
        libro.setStockTotal(request.getStockTotal());
        libro.setStockDisponible(nuevoStockDisponible);

        return mapToResponse(libroRepository.save(libro));
    }

    public void eliminarLibro(Long id) {
        Libro libro = libroRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        if (libro.getStockDisponible() < libro.getStockTotal()) {
            throw new BusinessRuleException("No se puede eliminar un libro con préstamos activos");
        }

        libro.setActivo(false); // Borrado lógico
        libroRepository.save(libro);
    }

    private LibroResponse mapToResponse(Libro libro) {
        return LibroResponse.builder()
                .id(libro.getId())
                .isbn(libro.getIsbn())
                .titulo(libro.getTitulo())
                .autor(libro.getAutor())
                .categoria(libro.getCategoria())
                .stockTotal(libro.getStockTotal())
                .stockDisponible(libro.getStockDisponible())
                .build();
    }
}