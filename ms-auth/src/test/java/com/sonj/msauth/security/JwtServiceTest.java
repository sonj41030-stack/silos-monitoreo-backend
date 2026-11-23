package com.sonj.msauth.security;

import com.sonj.msauth.entity.RolUsuario;
import com.sonj.msauth.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRETO = "clave-de-prueba-para-jwt-con-mas-de-32-bytes-123456";

    private Usuario usuario() {
        return Usuario.builder()
                .id("u1").nombre("Carlos Operador").email("operador@silos.com").passwordHash("x")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).rol(RolUsuario.OPERADOR).build();
    }

    @Test
    void tokenGenerado_seValidaYTraeLosDatos() {
        JwtService svc = new JwtService(SECRETO, 60000);

        Claims c = svc.validar(svc.generarToken(usuario()));

        assertEquals("operador@silos.com", c.getSubject());
        assertEquals("OPERADOR", c.get("rol", String.class));
        assertEquals("Carlos Operador", c.get("nombre", String.class));
    }

    @Test
    void tokenFirmadoConOtraClave_noValida() {
        JwtService emisor = new JwtService("otra-clave-distinta-con-mas-de-32-bytes-abcdef-999", 60000);
        JwtService receptor = new JwtService(SECRETO, 60000);

        String token = emisor.generarToken(usuario());

        assertThrows(JwtException.class, () -> receptor.validar(token));
    }

    @Test
    void tokenExpirado_noValida() {
        JwtService svc = new JwtService(SECRETO, -1000);

        String token = svc.generarToken(usuario());

        assertThrows(JwtException.class, () -> svc.validar(token));
    }

    @Test
    void tokenMalformado_noValida() {
        JwtService svc = new JwtService(SECRETO, 60000);

        assertThrows(JwtException.class, () -> svc.validar("esto.no.es-un-token"));
    }
}