package com.ddeleon2022166.BiblioApp.service;

import com.ddeleon2022166.BiblioApp.dto.request.AuthRequest;
import com.ddeleon2022166.BiblioApp.dto.request.RegisterRequest;
import com.ddeleon2022166.BiblioApp.dto.response.AuthResponse;
import com.ddeleon2022166.BiblioApp.entity.Usuario;
import com.ddeleon2022166.BiblioApp.enums.EstadoUsuario;
import com.ddeleon2022166.BiblioApp.enums.Rol;
import com.ddeleon2022166.BiblioApp.exception.BusinessRuleException;
import com.ddeleon2022166.BiblioApp.repository.UsuarioRepository;
import com.ddeleon2022166.BiblioApp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .estado(EstadoUsuario.ACTIVO)
                .rol(Rol.LECTOR) // Regla de negocio: Obligatorio predeterminado LECTOR
                .build();

        usuarioRepository.save(usuario);
        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessRuleException("Credenciales inválidas"));

        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }
}