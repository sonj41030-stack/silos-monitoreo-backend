package com.sonj.authservice.service;

import com.sonj.authservice.dto.*;
import com.sonj.authservice.model.Usuario;
import com.sonj.authservice.repository.UsuarioRepository;
import com.sonj.authservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public ResponseEntity<LoginResponse> login(LoginRequest req) {
        Optional<Usuario> opt = repo.findByEmail(req.email().trim().toLowerCase());
        if (opt.isEmpty() || !opt.get().isActivo()
                || !encoder.matches(req.contrasena(), opt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Correo o contraseña incorrectos", null, null, null));
        }
        Usuario u = opt.get();
        if (req.rol() != null && !req.rol().isBlank()
                && !req.rol().equalsIgnoreCase(u.getRol().name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse(false, "El rol seleccionado no corresponde al usuario", null, null, null));
        }
        return ResponseEntity.ok(new LoginResponse(true, "Inicio de sesión exitoso",
                jwt.generarToken(u), u.getNombre(), u.getRol().name()));
    }

    public UsuarioResponse registrar(RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (repo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        Usuario u = repo.save(new Usuario(req.nombre().trim(), email,
                encoder.encode(req.contrasena()), req.rol()));
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name());
    }

    public UsuarioResponse perfil(String email) {
        Usuario u = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().name());
    }
}