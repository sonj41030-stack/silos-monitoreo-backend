package com.sonj.msauth.service;

import com.sonj.msauth.dto.LoginRequest;
import com.sonj.msauth.dto.LoginResponse;
import com.sonj.msauth.dto.RegisterRequest;
import com.sonj.msauth.entity.RolUsuario;
import com.sonj.msauth.entity.Usuario;
import com.sonj.msauth.exception.ApiException;
import com.sonj.msauth.repository.UsuarioRepository;
import com.sonj.msauth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository repo;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthService service;

    private RegisterRequest registro(String email, String clave, LocalDate nacimiento) {
        return new RegisterRequest("Juan Pérez", email, clave, nacimiento, RolUsuario.OPERADOR);
    }

    private Usuario usuario() {
        return Usuario.builder()
                .id("1")
                .nombre("Carlos Operador")
                .email("operador@silos.com")
                .passwordHash("HASH")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .rol(RolUsuario.OPERADOR)
                .build();
    }

    @Test
    void registrarUsuarioValidoDevuelveToken() {
        when(repo.existsByEmailIgnoreCase("juan@silos.com")).thenReturn(false);
        when(encoder.encode("secreto1")).thenReturn("HASH");
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("TOKEN");

        LoginResponse r = service.registrar(registro("juan@silos.com", "secreto1", LocalDate.of(1995, 5, 5)));

        assertTrue(r.exito());
        assertEquals("TOKEN", r.token());
        assertEquals("OPERADOR", r.rol());
        verify(repo).save(any(Usuario.class));
    }

    @Test
    void registrarConEmailInvalidoLanza400() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.registrar(registro("correo-malo", "secreto1", LocalDate.of(1995, 5, 5))));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(repo, never()).save(any());
    }

    @Test
    void registrarConClaveCortaLanza400() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.registrar(registro("juan@silos.com", "123", LocalDate.of(1995, 5, 5))));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void registrarMenorDeEdadLanza400() {
        ApiException ex = assertThrows(ApiException.class,
                () -> service.registrar(registro("juan@silos.com", "secreto1", LocalDate.now().minusYears(15))));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("mayor de 18"));
    }

    @Test
    void registrarEmailDuplicadoLanza409() {
        when(repo.existsByEmailIgnoreCase("juan@silos.com")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.registrar(registro("juan@silos.com", "secreto1", LocalDate.of(1995, 5, 5))));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void loginCorrectoDevuelveToken() {
        when(repo.findByEmailIgnoreCase("operador@silos.com")).thenReturn(Optional.of(usuario()));
        when(encoder.matches("operador123", "HASH")).thenReturn(true);
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("TOKEN");

        LoginResponse r = service.login(new LoginRequest("operador@silos.com", "operador123", RolUsuario.OPERADOR));

        assertTrue(r.exito());
        assertEquals("TOKEN", r.token());
        assertEquals("Carlos Operador", r.usuarioNombre());
    }

    @Test
    void loginConUsuarioInexistenteLanza401() {
        when(repo.findByEmailIgnoreCase("nadie@silos.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.login(new LoginRequest("nadie@silos.com", "x", RolUsuario.OPERADOR)));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void loginConClaveIncorrectaLanza401() {
        when(repo.findByEmailIgnoreCase("operador@silos.com")).thenReturn(Optional.of(usuario()));
        when(encoder.matches("mala", "HASH")).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.login(new LoginRequest("operador@silos.com", "mala", RolUsuario.OPERADOR)));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void loginConRolDistintoLanza401() {
        when(repo.findByEmailIgnoreCase("operador@silos.com")).thenReturn(Optional.of(usuario()));
        when(encoder.matches("operador123", "HASH")).thenReturn(true);

        ApiException ex = assertThrows(ApiException.class,
                () -> service.login(new LoginRequest("operador@silos.com", "operador123", RolUsuario.ADMINISTRADOR)));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertTrue(ex.getMessage().contains("OPERADOR"));
    }
}
