package com.sonj.msauth.service;

import com.sonj.msauth.dto.LoginRequest;
import com.sonj.msauth.dto.LoginResponse;
import com.sonj.msauth.dto.RegisterRequest;
import com.sonj.msauth.dto.UsuarioResponse;
import com.sonj.msauth.entity.Usuario;
import com.sonj.msauth.exception.ApiException;
import com.sonj.msauth.repository.UsuarioRepository;
import com.sonj.msauth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public LoginResponse registrar(RegisterRequest req) {
        String email = req.email().trim();

        if (!EMAIL.matcher(email).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Por favor ingresa un correo electrónico válido.");
        }
        if (req.contrasena().length() < 6) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres.");
        }
        int edad = calcularEdad(req.fechaNacimiento());
        if (edad < 18) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Debes ser mayor de 18 años para registrarte (Edad actual: " + edad + " años).");
        }
        if (repo.existsByEmailIgnoreCase(email)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Ya existe una cuenta registrada con el correo " + email + ".");
        }

        Usuario usuario = Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nombre(req.nombre().trim())
                .email(email)
                .passwordHash(encoder.encode(req.contrasena()))
                .fechaNacimiento(req.fechaNacimiento())
                .rol(req.rol())
                .build();
        repo.save(usuario);

        return new LoginResponse(true, "Cuenta creada exitosamente.",
                jwtService.generarToken(usuario), usuario.getNombre(), usuario.getRol().name());
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.email().trim();

        if (!EMAIL.matcher(email).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El formato de correo electrónico no es válido.");
        }

        Usuario usuario = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "El correo no está registrado en el sistema. Regístrate primero."));

        if (!encoder.matches(req.contrasena(), usuario.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta. Revisa tus datos.");
        }
        if (usuario.getRol() != req.rol()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    "El correo registrado pertenece al rol " + usuario.getRol().name()
                            + ", no a " + req.rol().name() + ".");
        }
        if (!usuario.isActivo() || calcularEdad(usuario.getFechaNacimiento()) < 18) {
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    "Acceso denegado: El usuario debe estar activo y ser mayor de 18 años.");
        }

        return new LoginResponse(true, "Inicio de sesión exitoso.",
                jwtService.generarToken(usuario), usuario.getNombre(), usuario.getRol().name());
    }

    public UsuarioResponse perfil(String email) {
        Usuario u = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getFechaNacimiento(), u.getRol().name());
    }

    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
