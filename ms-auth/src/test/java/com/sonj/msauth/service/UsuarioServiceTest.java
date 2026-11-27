package com.sonj.msauth.service;

import com.sonj.msauth.dto.UsuarioAdminResponse;
import com.sonj.msauth.entity.RolUsuario;
import com.sonj.msauth.entity.Usuario;
import com.sonj.msauth.exception.ApiException;
import com.sonj.msauth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String ADMIN_EMAIL = "admin@silos.com";
    private static final String OPERADOR_EMAIL = "operador@silos.com";

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioService service;

    private Usuario admin;
    private Usuario operador;

    @BeforeEach
    void setUp() {
        admin = usuario("a1", ADMIN_EMAIL, RolUsuario.ADMINISTRADOR, true);
        operador = usuario("o1", OPERADOR_EMAIL, RolUsuario.OPERADOR, true);
    }

    private Usuario usuario(String id, String email, RolUsuario rol, boolean activo) {
        return Usuario.builder()
                .id(id).nombre("Nombre " + id).email(email).passwordHash("hash")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).rol(rol).activo(activo).build();
    }

    private void loginComoAdmin() {
        when(repo.findByEmailIgnoreCase(ADMIN_EMAIL)).thenReturn(Optional.of(admin));
    }

    @Test
    void listar_comoAdmin_devuelveUsuarios() {
        loginComoAdmin();
        when(repo.findAll(any(Sort.class))).thenReturn(List.of(admin, operador));

        List<UsuarioAdminResponse> r = service.listar(ADMIN_EMAIL);

        assertEquals(2, r.size());
        assertEquals("ADMINISTRADOR", r.get(0).rol());
    }

    @Test
    void listar_comoOperador_da403() {
        when(repo.findByEmailIgnoreCase(OPERADOR_EMAIL)).thenReturn(Optional.of(operador));

        ApiException ex = assertThrows(ApiException.class, () -> service.listar(OPERADOR_EMAIL));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void listar_adminDesactivado_da403() {
        admin.setActivo(false);
        loginComoAdmin();

        ApiException ex = assertThrows(ApiException.class, () -> service.listar(ADMIN_EMAIL));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    void listar_usuarioInexistente_da401() {
        when(repo.findByEmailIgnoreCase("nadie@silos.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> service.listar("nadie@silos.com"));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void cambiarRol_ok_actualizaYGuarda() {
        loginComoAdmin();
        when(repo.findById("o1")).thenReturn(Optional.of(operador));

        UsuarioAdminResponse r = service.cambiarRol(ADMIN_EMAIL, "o1", RolUsuario.JEFATURA);

        assertEquals("JEFATURA", r.rol());
        verify(repo).save(operador);
    }

    @Test
    void cambiarRol_usuarioInexistente_da404() {
        loginComoAdmin();
        when(repo.findById("zzz")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> service.cambiarRol(ADMIN_EMAIL, "zzz", RolUsuario.JEFATURA));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void cambiarRol_propioRol_da409() {
        loginComoAdmin();
        when(repo.findById("a1")).thenReturn(Optional.of(admin));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.cambiarRol(ADMIN_EMAIL, "a1", RolUsuario.OPERADOR));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void desactivar_ok_marcaInactivo() {
        loginComoAdmin();
        when(repo.findById("o1")).thenReturn(Optional.of(operador));

        UsuarioAdminResponse r = service.cambiarEstado(ADMIN_EMAIL, "o1", false);

        assertFalse(r.activo());
        verify(repo).save(operador);
    }

    @Test
    void desactivar_propiaCuenta_da409() {
        loginComoAdmin();
        when(repo.findById("a1")).thenReturn(Optional.of(admin));

        ApiException ex = assertThrows(ApiException.class,
                () -> service.cambiarEstado(ADMIN_EMAIL, "a1", false));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void activar_ok_marcaActivo() {
        operador.setActivo(false);
        loginComoAdmin();
        when(repo.findById("o1")).thenReturn(Optional.of(operador));

        UsuarioAdminResponse r = service.cambiarEstado(ADMIN_EMAIL, "o1", true);

        assertTrue(r.activo());
    }
}