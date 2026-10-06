package com.ddeleon2022166.BiblioApp.config;

import com.ddeleon2022166.BiblioApp.entity.Libro;
import com.ddeleon2022166.BiblioApp.entity.Usuario;
import com.ddeleon2022166.BiblioApp.enums.EstadoUsuario;
import com.ddeleon2022166.BiblioApp.enums.Rol;
import com.ddeleon2022166.BiblioApp.repository.LibroRepository;
import com.ddeleon2022166.BiblioApp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeedConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, LibroRepository libroRepository) {
        return args -> {

            // 1. Crear Admin si no existe
            if (!usuarioRepository.existsByEmail("admin@biblioteca.edu")) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador Principal")
                        .email("admin@biblioteca.edu")
                        .password(passwordEncoder.encode("admin123")) // <-- Encriptación dinámica real
                        .estado(EstadoUsuario.ACTIVO)
                        .rol(Rol.ADMIN)
                        .build();
                usuarioRepository.save(admin);
                System.out.println("-Usuario ADMIN creado dinámicamente.");
            }

            // 2. Crear Bibliotecario si no existe
            if (!usuarioRepository.existsByEmail("bibliotecario@biblioteca.edu")) {
                Usuario bibliotecario = Usuario.builder()
                        .nombre("Bibliotecario Inicial")
                        .email("bibliotecario@biblioteca.edu")
                        .password(passwordEncoder.encode("admin123")) // <-- Encriptación dinámica real
                        .estado(EstadoUsuario.ACTIVO)
                        .rol(Rol.BIBLIOTECARIO)
                        .build();
                usuarioRepository.save(bibliotecario);
                System.out.println("-Usuario BIBLIOTECARIO creado dinámicamente.");
            }

            // 3. Crear Libro de prueba si el catálogo está vacío
            if (!libroRepository.existsByIsbnAndActivoTrue("978-0134685991")) {
                Libro libro = Libro.builder()
                        .isbn("978-0134685991")
                        .titulo("Effective Java")
                        .autor("Joshua Bloch")
                        .categoria("Programación")
                        .stockTotal(5)
                        .stockDisponible(5)
                        .activo(true)
                        .build();
                libroRepository.save(libro);
                System.out.println("-Libro de prueba creado dinámicamente.");
            }
        };
    }
}